package com.biit.ks.core.opensearch;

import com.biit.ks.core.opensearch.exceptions.OpenSearchConnectionException;
import com.biit.ks.logger.OpenSearchLogger;
import com.biit.ks.logger.SolrLogger;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.core.DeleteResponse;
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

@Component
public class OpenSearchClient {

    private static final int DEFAULT_OPENSEARCH_PORT = 9200;
    private static final String DEFAULT_EXPAND_REPLICAS = "0-all";

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
        final RestClient restClient = RestClient.builder(host).
                setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();

        final OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new org.opensearch.client.opensearch.OpenSearchClient(transport);
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

    public <I> SearchResponse<I> searchData(Class<I> indexDataClass, SearchRequest request) {
        try {
            final SearchResponse<I> searchResponse = client.search(request, indexDataClass);
            for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
                OpenSearchLogger.debug(this.getClass(), searchResponse.hits().hits().get(i).source() + "");
            }
            return searchResponse;
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

    public DeleteResponse deleteData(String indexName, String id) {
        try {
            return client.delete(b -> b.index(indexName).id(id));
        } catch (IOException e) {
            throw new OpenSearchConnectionException(this.getClass(), e);
        }
    }

}
