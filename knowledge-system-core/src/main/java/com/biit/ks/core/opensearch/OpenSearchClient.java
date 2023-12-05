package com.biit.ks.core.opensearch;

import com.biit.ks.core.opensearch.exceptions.OpenSearchConnectionException;
import com.biit.ks.logger.OpenSearchLogger;
import com.biit.ks.logger.SolrLogger;
import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;
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
import java.util.List;

@Component
public class OpenSearchClient {

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

        final OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
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
        }
    }

    public <I> IndexResponse indexData(I indexData, String indexName, String id) {
        try {
            final IndexRequest<I> indexRequest = new IndexRequest.Builder<I>().index(indexName).id(id).document(indexData).build();
            return client.index(indexRequest);
        } catch (IOException e) {
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
        try {
            final SearchResponse<I> searchResponse = client.search(s -> {
                s.query(query);
                return s;
            }, dataClass);

            final List<I> output = new ArrayList<>();
            for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
                output.add(searchResponse.hits().hits().get(i).source());
            }
            return searchResponse;
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
//    public <I> SearchResponse<I> searchData(Class<I> dataClass, String indexName, String field, String query, int size) throws IOException {
//        // Create a search request
//        final SearchRequest searchRequest = new SearchRequest.Builder().index(indexName).build();
//        searchRequest.source().query(new SearchSourceBuilder().QueryBuilders.match().field(field).query(FieldValue.of(query)).build()).size(size);
//
//        // Execute the search request
//        return client.search(searchRequest, dataClass);
//    }
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

}
