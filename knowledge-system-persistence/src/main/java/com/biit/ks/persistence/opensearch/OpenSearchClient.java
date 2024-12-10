package com.biit.ks.persistence.opensearch;

import com.biit.ks.logger.OpenSearchLogger;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchConnectionException;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchIndexMissingException;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchInvalidSearchQueryException;
import com.biit.ks.persistence.opensearch.search.IntervalsSearch;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PreDestroy;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.Intervals;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsMatch;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsPrefix;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsQuery;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsQueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsWildcard;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch.core.CountRequest;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.DeleteByQueryRequest;
import org.opensearch.client.opensearch.core.DeleteByQueryResponse;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.GetRequest;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.UpdateRequest;
import org.opensearch.client.opensearch.core.UpdateResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class OpenSearchClient {

    private static final int MAX_SEARCH_RESULTS = 100;
    private static final int MAX_CONNECTION_RETRIES = 10;
    private static final int DEFAULT_OPENSEARCH_PORT = 9200;
    private static final int MAX_CONNECTIONS = 10;
    private static final String DEFAULT_EXPAND_REPLICAS = "0-all";

    private RestClient restClient;

    private org.opensearch.client.opensearch.OpenSearchClient client;

    private final String scheme;
    private final String server;
    private final String pathPrefix;
    private final String serverPort;
    private final String user;
    private final String password;
    private final boolean skipSslCheck;

    private int connectionsRetried = 0;


    public OpenSearchClient(@Value("${opensearch.scheme}") String scheme,
                            @Value("${opensearch.server}") String server,
                            @Value("${opensearch.pathPrefix:#{null}}") String pathPrefix,
                            @Value("${opensearch.port}") String serverPort,
                            @Value("${opensearch.user}") String user,
                            @Value("${opensearch.password}") String password,
                            @Value("${opensearch.truststore.path:#{null}}") String truststorePath,
                            @Value("${opensearch.truststore.password:#{null}}") String truststorePassword,
                            @Value("${opensearch.insecure.skip.verify:#{false}}") boolean skipSslCheck) {
        this.scheme = scheme;
        this.server = server;
        this.pathPrefix = pathPrefix;
        this.serverPort = serverPort;
        this.user = user;
        this.password = password;
        this.skipSslCheck = skipSslCheck;

        if (truststorePath != null && !truststorePath.isBlank() && !skipSslCheck) {
            //Include lestencrypt root certificate 'isrgrootx1.der'
            // on the trustStore to allow connections with user manager from localhost.
            System.setProperty("javax.net.ssl.trustStore", truststorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);

            OpenSearchLogger.debug(this.getClass(), "Reading certificates from '{}'.", truststorePath);

            if (!Files.exists(Path.of(truststorePath))) {
                OpenSearchLogger.severe(this.getClass(), "Certificates not found at '{}'.", truststorePath);
            }
        }
        connect();
    }


    private void connect() {
        int convertedPort;
        if (serverPort != null) {
            try {
                convertedPort = Integer.parseInt(serverPort);
            } catch (NumberFormatException e) {
                OpenSearchLogger.severe(this.getClass(), "Invalid port number '{}'", serverPort);
                convertedPort = DEFAULT_OPENSEARCH_PORT;
            }
        } else {
            convertedPort = DEFAULT_OPENSEARCH_PORT;
        }

        final HttpHost host = new HttpHost(server, convertedPort, scheme);
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        //Set user credentials
        credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(user, password));

        //Initialize the client with SSL and TLS enabled
        final RestClientBuilder restClientBuilder = RestClient.builder(host).
                setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                                .setMaxConnTotal(MAX_CONNECTIONS));

        if (pathPrefix != null && !pathPrefix.isBlank()) {
            restClientBuilder.setPathPrefix(pathPrefix);
        }

        if (skipSslCheck) {
            restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                    httpAsyncClientBuilder.setSSLHostnameVerifier((s, sslSession) -> true));
        }

        restClient = restClientBuilder.build();

        //For LocalDateTime usage
        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        final JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        final OpenSearchTransport transport = new RestClientTransport(restClient, jsonpMapper);
        client = new org.opensearch.client.opensearch.OpenSearchClient(transport);
    }

    @PreDestroy
    public void close() {
        try {
            OpenSearchLogger.info(this.getClass(), "Closing the client....");
            restClient.close();
            OpenSearchLogger.info(this.getClass(), "Client closed successfully!");
        } catch (IOException e) {
            OpenSearchLogger.errorMessage(this.getClass(), e);
        }
    }


    public boolean reconnect() {
        try {
            if (connectionsRetried < MAX_CONNECTION_RETRIES) {
                connectionsRetried++;
                close();
                connect();
                return true;
            }
        } catch (Exception e) {
            OpenSearchLogger.errorMessage(this.getClass(), e);
        }
        return false;
    }

    @Scheduled(cron = "@midnight")
    public void cleanUpConnectionsCounter() {
        connectionsRetried = 0;
    }


    public void refreshIndex() {
        try {
            OpenSearchLogger.debug(this.getClass(), "Refreshing index...");
            client.indices().refresh();
        } catch (IOException e) {
            OpenSearchLogger.severe(this.getClass(), "Failed to refresh index!");
            OpenSearchLogger.errorMessage(this.getClass(), e);
        }
    }

    public PutIndicesSettingsResponse createIndex(String indexName) {
        try {
            final TypeMapping mappings = TypeMapping.of(mappingsBuilder -> {
                for (String sortingProperty : SortResultOptions.ALLOWED_FIELDS) {
                    mappingsBuilder.properties(sortingProperty, type -> type.keyword(keyword -> keyword));
                }
                return mappingsBuilder;
            });
            client.indices().create(idxBuilder -> idxBuilder.index(indexName).mappings(mappings));

            final IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas(DEFAULT_EXPAND_REPLICAS).build();
            final PutIndicesSettingsRequest putIndicesSettingsRequest = new PutIndicesSettingsRequest.Builder().index(indexName)
                    .settings(indexSettings).build();
            return client.indices().putSettings(putIndicesSettingsRequest);
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return createIndex(indexName);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }


    public DeleteIndexResponse deleteIndex(String indexName) {
        try {
            final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(indexName).build();
            OpenSearchLogger.warning(this.getClass(), "Deleting index '{}'", indexName);
            return client.indices().delete(deleteIndexRequest);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            if (!e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchConnectionException(this.getClass(), e);
            }
        }
        return new DeleteIndexResponse.Builder().acknowledged(false).build();
    }


    public <I> IndexResponse indexData(I data, String indexName, String id) {
        try {
            final IndexRequest<I> indexRequest = new IndexRequest.Builder<I>().index(indexName).id(id).document(data).build();
            return client.index(indexRequest);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }


    public <I> GetResponse<I> getData(Class<I> dataClass, String indexName, String id) {
        try {
            //final GetRequest getRequest = new GetRequest(indexName, id);
            final GetRequest getRequest = new GetRequest.Builder().index(indexName).id(id).build();
            return client.get(getRequest, dataClass);
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return getData(dataClass, indexName, id);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }


    public <I> UpdateResponse<I> updateData(Class<I> dataClass, I data, String indexName, String id) {
        try {
            final UpdateRequest<I, I> indexRequest = new UpdateRequest.Builder<I, I>().index(indexName).id(id).doc(data).build();
            return client.update(indexRequest, dataClass);
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return updateData(dataClass, data, indexName, id);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }


    public <I> I getElement(Class<I> indexDataClass, String indexName) {
        try {
            final SearchResponse<I> searchResponse = client.search(s -> s.index(indexName), indexDataClass);
            if (!searchResponse.hits().hits().isEmpty()) {
                return searchResponse.hits().hits().get(0).source();
            }
            return null;
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return getElement(indexDataClass, indexName);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public <I> SearchResponse<I> searchData(Class<I> indexDataClass, String indexName) {
        return searchData(indexDataClass, indexName, (SortOptions) null, null, null);
    }

    public <I> SearchResponse<I> searchData(Class<I> indexDataClass, String indexName, SortOptions sortOptions, Integer from, Integer size) {
        try {
            final SearchResponse<I> searchResponse = client.search(
                    s -> {
                        s.index(indexName);
                        if (from != null) {
                            s.from(from);
                        }
                        if (size != null) {
                            s.size(size);
                        }
                        if (sortOptions != null) {
                            s.sort(List.of(sortOptions));
                        }
                        return s;
                    }, indexDataClass);
            for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
                OpenSearchLogger.debug(this.getClass(), searchResponse.hits().hits().get(i).source() + "");
            }
            return searchResponse;
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return searchData(indexDataClass, indexName, sortOptions, from, size);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Query failed. Parameters: Class '{}', Index '{}', Sorting '{}', From '{}', Size '{}'.",
                    indexDataClass, indexName, sortOptions, from, size);
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, SearchRequest request) {
        try {
            final SearchResponse<I> searchResponse = client.search(request, dataClass);
            for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
                OpenSearchLogger.debug(this.getClass(), searchResponse.hits().hits().get(i).source() + "");
            }
            return searchResponse;
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return searchData(dataClass, request);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Query failed. Parameters: Class '{}', SearchRequest '{}'.",
                    dataClass, request);
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, Query query) {
        return searchData(dataClass, indexName, query, null, null, null);
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, Query query, SortOptions sortOptions, Integer from, Integer size) {
        try {
            return client.search(s -> {
                if (indexName != null) {
                    s.index(indexName);
                }
                s.query(query);
                if (from != null) {
                    s.from(from);
                }
                if (size != null) {
                    s.size(size);
                }
                if (sortOptions != null) {
                    s.sort(List.of(sortOptions));
                }
                return s;
            }, dataClass);
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return searchData(dataClass, indexName, query, sortOptions, from, size);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Query failed. Parameters: Class '{}', Query '{}', Sorting '{}', From '{}', Size '{}'.",
                    dataClass, query, sortOptions, from, size);
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }


    public CountResponse countData(String indexName, Query query) {
        try {
            if (query == null) {
                return countData(indexName);
            }
            final CountRequest.Builder countQuery = new CountRequest.Builder().query(query);
            if (indexName != null) {
                countQuery.index(indexName);
            }
            return client.count(countQuery.build());
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return countData(indexName, query);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Count query failed. Parameters: Query '{}'.",
                    query);
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public DeleteByQueryResponse deleteData(String indexName, Query query) {
        try {
            final DeleteByQueryRequest.Builder deleteQuery = new DeleteByQueryRequest.Builder().query(query);
            if (indexName != null) {
                deleteQuery.index(indexName);
            }
            return client.deleteByQuery(deleteQuery.build());
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return deleteData(indexName, query);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Delete query failed. Parameters: Query '{}'.",
                    query);
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }


    public <I> SearchWrapper<I> convertResponse(SearchResponse<I> searchResponse) {
        final List<I> output = new ArrayList<>();
        if (searchResponse != null && searchResponse.hits() != null && searchResponse.hits().hits() != null) {
            for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
                output.add(searchResponse.hits().hits().get(i).source());
            }
        }
        final SearchWrapper<I> searchWrapper = new SearchWrapper<>(output);
        if (searchResponse != null && searchResponse.hits() != null) {
            searchWrapper.setTotalElements(searchResponse.hits().total().value());
        }
        return searchWrapper;
    }

    public long convertResponse(CountResponse countResponse) {
        return countResponse.count();
    }


    /**
     * Searches a document in Opensearch using the query builder.
     *
     * @param indexName The index to search in.
     * @param field     The field to search in.
     * @param query     The query string.
     * @param size      The maximum number of search hits to return.
     * @return The search response containing the search hits.
     * @throws IOException If an error occurs while performing the search.
     */
    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, String field, String query, SortOptions sortOptions,
                                            Integer from, Integer size) throws IOException {
        try {
            return client.search(s -> {
                s.index(indexName);
                s.query(q -> q.match(t -> t.field(field).query(FieldValue.of(query))));
                if (sortOptions != null) {
                    s.sort(List.of(sortOptions));
                }
                if (from != null) {
                    s.from(from);
                }
                if (size != null) {
                    s.size(size);
                }
                return s;
            }, dataClass);
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return searchData(dataClass, indexName, field, query, sortOptions, from, size);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Query failed. Parameters: Class '{}', Index '{}', Field '{}', Query '{}', Sorting '{}'"
                            + ", From '{}', Size '{}'",
                    dataClass, indexName, field, query, sortOptions, from, size);
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public DeleteResponse deleteData(String indexName, String id) {
        try {
            return client.delete(b -> b.index(indexName).id(id));
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return deleteData(indexName, id);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Delete query failed. Parameters: Index '{}', Id '{}'.", indexName, id);
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public <I> SearchResponse<I> getAll(Class<I> indexDataClass, String indexName) {
        return getAll(indexDataClass, indexName, null, null);
    }

    public <I> SearchResponse<I> getAll(Class<I> indexDataClass, String indexName, Integer from, Integer size) {
        return getAll(indexDataClass, indexName, null, from, size);
    }

    public <I> SearchResponse<I> getAll(Class<I> indexDataClass, String indexName, SortResultOptions sortResultOptions, Integer from, Integer size) {
        return searchData(indexDataClass, indexName,
                sortResultOptions != null && sortResultOptions.getField() != null ? sortResultOptions.convert() : null,
                from, size);
    }

    public CountResponse countData(String indexName) {
        try {
            final CountRequest.Builder countQuery = new CountRequest.Builder();
            if (indexName != null) {
                countQuery.index(indexName);
            }
            return client.count(countQuery.build());
        } catch (ConnectionClosedException e) {
            OpenSearchLogger.warning(this.getClass(), "Opensearch client gets error message: {}", e.getMessage());
            if (reconnect()) {
                return countData(indexName);
            }
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            OpenSearchLogger.severe(this.getClass(), "Count failed.");
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
            }
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchInvalidSearchQueryException(this.getClass(), e);
            }
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, SearchPredicates searchPredicates) {
        return searchData(dataClass, indexName, searchPredicates, null);
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, SearchPredicates searchPredicates, SortResultOptions sortResultOptions) {
        return searchData(indexName, new SearchQuery<>(dataClass, sortResultOptions, searchPredicates));
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, SearchPredicates searchPredicates, Integer from, Integer size) {
        return searchData(dataClass, indexName, searchPredicates, null, from, size);
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, SearchPredicates searchPredicates, SortResultOptions sortResultOptions,
                                            Integer from, Integer size) {
        return searchData(indexName, new SearchQuery<>(dataClass, sortResultOptions, from, size, searchPredicates));
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, IntervalsSearch intervalsSearch) {
        return searchData(indexName, new SearchQuery<>(dataClass, intervalsSearch));
    }


    public <I> SearchResponse<I> searchData(String indexName, SearchQuery<I> searchQuery) {
        return searchData(searchQuery.getDataClass(), indexName, createQuery(searchQuery),
                searchQuery.getSortResultOptions() != null && searchQuery.getSortResultOptions().getField() != null
                        ? searchQuery.getSortResultOptions().convert() : null,
                searchQuery.getFrom(), searchQuery.getSize());
    }


    public <I> CountResponse countData(Class<I> dataClass, String indexName, SearchPredicates searchPredicates) {
        return countData(indexName, new SearchQuery<>(dataClass, searchPredicates));
    }


    public <I> CountResponse countData(String indexName, SearchQuery<I> searchQuery) {
        return countData(indexName, createQuery(searchQuery));
    }


    public <I> DeleteByQueryResponse deleteData(String indexName, SearchQuery<I> searchQuery) {
        return deleteData(indexName, createQuery(searchQuery));
    }


    public <I> DeleteByQueryResponse deleteData(Class<I> dataClass, String indexName, SearchPredicates searchPredicates) {
        return deleteData(indexName, new SearchQuery<>(dataClass, searchPredicates));
    }


    private <I> Query createQuery(SearchQuery<I> searchQuery) {
        final List<Query> mustHaveQueries = createQuery(searchQuery.getMustHaveValues());
        final List<Query> mustNotHaveQueries = createQuery(searchQuery.getMustNotHaveValues());
        final List<Query> shouldHaveQueries = createQuery(searchQuery.getShouldHaveValues());
        final List<Query> filter = createQuery(searchQuery.getFilters());
        final List<Query> intervalsSearch = createQuery(searchQuery.getIntervals());

        final BoolQuery.Builder builder = new BoolQuery.Builder().must(mustHaveQueries).mustNot(mustNotHaveQueries).should(shouldHaveQueries).filter(filter)
                .must(intervalsSearch);

        if (searchQuery.getShouldHaveValues() != null && searchQuery.getMinimumShouldMatch() != null) {
            builder.minimumShouldMatch(String.valueOf(searchQuery.getMinimumShouldMatch()));
        }
        return builder.build()._toQuery();
    }


    private <E extends SearchPredicates> List<Query> createQuery(Collection<E> searchParameters) {
        final List<Query> searchQuery = new ArrayList<>();
        if (searchParameters != null && !searchParameters.isEmpty()) {
            for (SearchPredicates searchParameter : searchParameters) {
                searchParameter.getSearch().forEach(stringPair -> {
                    if (stringPair.getRight() != null) {
                        final MatchQuery.Builder builder = new MatchQuery.Builder().field(stringPair.getLeft())
                                .query(FieldValue.of(stringPair.getRight()));
                        if (searchParameter.getFuzzinessDefinition() != null) {
                            builder.fuzziness(searchParameter.getFuzzinessDefinition().getFuzziness().tag());
                            if (searchParameter.getFuzzinessDefinition().getMaxExpansions() != null) {
                                builder.maxExpansions(searchParameter.getFuzzinessDefinition().getMaxExpansions());
                            }
                            if (searchParameter.getFuzzinessDefinition().getPrefixLength() != null) {
                                builder.prefixLength(searchParameter.getFuzzinessDefinition().getPrefixLength());
                            }
                        }
                        searchQuery.add(builder.build()._toQuery());
                    } else {
                        //Search by field that is null.
                        final BoolQuery nullParameter = new BoolQuery.Builder().mustNot(new ExistsQuery.Builder()
                                .field(stringPair.getLeft()).build()._toQuery()).build();
                        searchQuery.add(nullParameter._toQuery());
                    }
                });
                searchParameter.getCategories().forEach(category -> {
                    if (category.getRight() != null) {
                        final MatchQuery.Builder builder = new MatchQuery.Builder().field(category.getLeft())
                                .query(FieldValue.of(category.getRight()));
                        searchQuery.add(builder.build()._toQuery());
                    } else {
                        //Search by field that is null.
                        final BoolQuery nullParameter = new BoolQuery.Builder().mustNot(new ExistsQuery.Builder()
                                .field(category.getLeft()).build()._toQuery()).build();
                        searchQuery.add(nullParameter._toQuery());
                    }
                });
                searchParameter.getMultiSearch().forEach(listStringPair -> {
                    final MultiMatchQuery.Builder builder = new MultiMatchQuery.Builder().fields(listStringPair.getLeft())
                            .query(listStringPair.getRight());
                    if (searchParameter.getFuzzinessDefinition() != null) {
                        builder.fuzziness(searchParameter.getFuzzinessDefinition().getFuzziness().tag());
                        if (searchParameter.getFuzzinessDefinition().getMaxExpansions() != null) {
                            builder.maxExpansions(searchParameter.getFuzzinessDefinition().getMaxExpansions());
                        }
                        if (searchParameter.getFuzzinessDefinition().getPrefixLength() != null) {
                            builder.prefixLength(searchParameter.getFuzzinessDefinition().getPrefixLength());
                        }
                    }
                    searchQuery.add(builder.build()._toQuery());
                });
                searchParameter.getRanges().forEach(range -> {
                    final RangeQuery.Builder builder = new RangeQuery.Builder().field(range.getParameter());

                    if (range.getLt() != null) {
                        builder.lt(JsonData.of(range.getLt()));
                    }
                    if (range.getLte() != null) {
                        builder.lte(JsonData.of(range.getLte()));
                    }
                    if (range.getGt() != null) {
                        builder.gt(JsonData.of(range.getGt()));
                    }
                    if (range.getGte() != null) {
                        builder.gte(JsonData.of(range.getGte()));
                    }

                    searchQuery.add(builder.build()._toQuery());
                });
            }
        }
        return searchQuery;
    }

    private List<Query> createQuery(IntervalsSearch intervalsSearch) {
        final List<Intervals> intervals = new ArrayList<>();
        if (intervalsSearch != null) {
            //Prefix search.
            intervalsSearch.getPrefixes().forEach(prefix -> {
                if (prefix.getField() != null) {
                    intervals.add(new IntervalsPrefix.Builder().useField(prefix.getField()).prefix(prefix.getPrefix()).build()._toIntervals());
                }
            });

            //Match search.
            intervalsSearch.getMatches().forEach(match -> {
                if (match.getField() != null) {
                    final IntervalsMatch.Builder intervalsMatchBuilder = new IntervalsMatch.Builder().useField(match.getField()).query(match.getQuery());
                    if (match.getMaxGap() != null) {
                        intervalsMatchBuilder.maxGaps(match.getMaxGap());
                    }
                    if (match.getOrdered() != null) {
                        intervalsMatchBuilder.ordered(match.getOrdered());
                    }
                    intervals.add(intervalsMatchBuilder.build()._toIntervals());
                }
            });

            //Wildcard search.
            intervalsSearch.getWildcards().forEach(wildcards -> {
                if (wildcards.getField() != null) {
                    final IntervalsWildcard.Builder intervalsWildcardBuilder = new IntervalsWildcard.Builder()
                            .useField(wildcards.getField()).pattern(wildcards.getPattern());
                    intervals.add(intervalsWildcardBuilder.build()._toIntervals());
                }
            });
            if (intervalsSearch.getIntervalsSearchOperator() == QuantifiersOperator.ANY_OF) {
                //Why I need a field here, and can be any of the fields used?
                return List.of(new IntervalsQuery.Builder().field(intervalsSearch.getAnyField()).anyOf(IntervalsQueryBuilders.anyOf()
                        .intervals(intervals).build()).build()._toQuery());
            } else {
                //Why I need a field here, and can be any of the fields used?
                return List.of(new IntervalsQuery.Builder().field(intervalsSearch.getAnyField()).allOf(IntervalsQueryBuilders.allOf()
                        .intervals(intervals).build()).build()._toQuery());
            }
        }
        return new ArrayList<>();
    }

}
