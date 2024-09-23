package com.biit.ks.persistence.opensearch;

import com.biit.ks.persistence.opensearch.search.intervals.IntervalsSearchOperator;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchConnectionException;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchIndexMissingException;
import com.biit.ks.persistence.opensearch.search.IntervalsSearch;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.logger.OpenSearchLogger;
import com.biit.ks.logger.SolrLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
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
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsRequest;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class OpenSearchClient {

    private static final int MAX_SEARCH_RESULTS = 1000;
    private static final int DEFAULT_OPENSEARCH_PORT = 9200;
    private static final String DEFAULT_EXPAND_REPLICAS = "0-all";

    private final RestClient restClient;

    private final org.opensearch.client.opensearch.OpenSearchClient client;

    public OpenSearchClient(@Value("${opensearch.scheme}") String scheme,
                            @Value("${opensearch.server}") String server,
                            @Value("${opensearch.port}") String serverPort,
                            @Value("${opensearch.user}") String user,
                            @Value("${opensearch.password}") String password) {

        int convertedPort;
        if (serverPort != null) {
            try {
                convertedPort = Integer.parseInt(serverPort);
            } catch (NumberFormatException e) {
                SolrLogger.severe(this.getClass(), "Invalid port number '{}'", serverPort);
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
        restClient = RestClient.builder(host).
                setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();

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

    public void refreshIndex() {
        try {
            client.indices().refresh();
        } catch (IOException e) {
            KnowledgeSystemLogger.severe(this.getClass(), "Failed to refresh index!");
            KnowledgeSystemLogger.errorMessage(this.getClass(), e);
        }
    }

    public PutIndicesSettingsResponse createIndex(String indexName) {
        try {
            final CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(indexName).build();
            client.indices().create(createIndexRequest);

            final IndexSettings indexSettings = new IndexSettings.Builder().autoExpandReplicas(DEFAULT_EXPAND_REPLICAS).build();
            final PutIndicesSettingsRequest putIndicesSettingsRequest = new PutIndicesSettingsRequest.Builder().index(indexName)
                    .settings(indexSettings).build();
            return client.indices().putSettings(putIndicesSettingsRequest);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public DeleteIndexResponse deleteIndex(String indexName) {
        try {
            final DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(indexName).build();
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

    public <I> IndexResponse indexData(I indexData, String indexName, String id) {
        try {
            final IndexRequest<I> indexRequest = new IndexRequest.Builder<I>().index(indexName).id(id).document(indexData).build();
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
        try {
            final SearchResponse<I> searchResponse = client.search(s -> s.index(indexName), indexDataClass);
            for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
                OpenSearchLogger.debug(this.getClass(), searchResponse.hits().hits().get(i).source() + "");
            }
            return searchResponse;
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        } catch (OpenSearchException e) {
            if (e.getMessage().contains("index_not_found_exception")) {
                throw new OpenSearchIndexMissingException(this.getClass(), e);
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
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, Query query) {
        return searchData(dataClass, query, null, null);
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, Query query, Integer from, Integer size) {
        try {
            return client.search(s -> {
                s.query(query);
                s.from(from != null ? from : 0);
                s.size(size != null ? size : MAX_SEARCH_RESULTS);
                return s;
            }, dataClass);
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public CountResponse countData(Query query) {
        try {
            return client.count(new CountRequest.Builder().query(query).build());
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public DeleteByQueryResponse deleteData(Query query) {
        try {
            return client.deleteByQuery(new DeleteByQueryRequest.Builder().query(query).build());
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }


    public <I> List<I> convertResponse(SearchResponse<I> searchResponse) {
        final List<I> output = new ArrayList<>();
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            output.add(searchResponse.hits().hits().get(i).source());
        }
        return output;
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
    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, String field, String query, int size) throws IOException {
        return client.search(s -> s.index(indexName).query(q -> q
                .match(t -> t.field(field).query(FieldValue.of(query)))).size(size), dataClass);
    }

    public DeleteResponse deleteData(String indexName, String id) {
        try {
            return client.delete(b -> b.index(indexName).id(id));
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, SearchPredicates shouldHaveValues) {
        return searchData(new SearchQuery<>(dataClass, shouldHaveValues));
    }

    public <I> SearchResponse<I> searchData(Class<I> dataClass, IntervalsSearch intervalsSearch) {
        return searchData(new SearchQuery<>(dataClass, intervalsSearch));
    }


    public <I> SearchResponse<I> searchData(SearchQuery<I> searchQuery) {
        return searchData(searchQuery.getDataClass(), createQuery(searchQuery), searchQuery.getFrom(), searchQuery.getSize());
    }

    public <I> CountResponse countData(SearchQuery<I> searchQuery) {
        return countData(createQuery(searchQuery));
    }

    public <I> DeleteByQueryResponse deleteData(SearchQuery<I> searchQuery) {
        return deleteData(createQuery(searchQuery));
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
                    final MatchQuery.Builder builder = new MatchQuery.Builder().field(stringPair.getFirst())
                            .query(FieldValue.of(stringPair.getSecond()));
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
                searchParameter.getMultiSearch().forEach(listStringPair -> {
                    final MultiMatchQuery.Builder builder = new MultiMatchQuery.Builder().fields(listStringPair.getFirst())
                            .query(listStringPair.getSecond());
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
            if (intervalsSearch.getIntervalsSearchOperator() == IntervalsSearchOperator.ANY_OF) {
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
