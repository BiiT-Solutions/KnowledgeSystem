package com.biit.ks.core.solr;

import com.biit.ks.logger.SolrLogger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class SolrClient {
    private static final int CLIENT_CONNECTION_TIMEOUT_SECONDS = 10;
    private final Http2SolrClient solrClient;
    private final Integer shards;
    private final Integer replicas;


    public SolrClient(String serverUrl, String shards, String replicas
    ) {
        SolrLogger.debug(this.getClass(), "Using url '{}'", serverUrl);
        this.solrClient = new Http2SolrClient.Builder(serverUrl)
                .withConnectionTimeout(CLIENT_CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS).build();

        Integer convertedShards = null;
        if (shards != null) {
            try {
                convertedShards = Integer.parseInt(shards);
                SolrLogger.debug(this.getClass(), "Using shards number '{}'", shards);
            } catch (NumberFormatException e) {
                SolrLogger.severe(this.getClass(), "Invalid shards number '{}'", shards);
            }
        }
        this.shards = convertedShards;

        Integer convertedReplicas = null;
        if (shards != null) {
            try {
                convertedReplicas = Integer.parseInt(replicas);
                SolrLogger.debug(this.getClass(), "Using replicas number '{}'", replicas);
            } catch (NumberFormatException e) {
                SolrLogger.severe(this.getClass(), "Invalid replicas number '{}'", replicas);
            }
        }
        this.replicas = convertedReplicas;
    }

    protected Http2SolrClient getSolrClient() {
        return solrClient;
    }

    /**
     * ONLY FOR SOLRCLOUD.
     *
     * @param collectionName    collection to set the config.
     * @param configSetName     the config file name.
     * @param baseConfigSetName no idea yet.
     * @throws SolrServerException
     * @throws IOException
     */
    public void setConfig(String collectionName, String configSetName, String baseConfigSetName) throws SolrServerException, IOException {
        final ConfigSetAdminRequest.Create adminRequest = new ConfigSetAdminRequest.Create();
        adminRequest.setConfigSetName(configSetName);
        adminRequest.setBaseConfigSetName(baseConfigSetName);
        final ConfigSetAdminResponse response = adminRequest.process(solrClient);
        SolrLogger.debug(this.getClass(), "Obtained response for 'setConfig': {}", response);
    }

    /**
     * ONLY FOR SOLRCLOUD.
     *
     * @param configSetName the config file name.
     * @throws SolrServerException
     * @throws IOException
     */
    public void deleteConfig(String configSetName) throws SolrServerException, IOException {
        final ConfigSetAdminRequest.Delete adminRequest = new ConfigSetAdminRequest.Delete();
        adminRequest.setConfigSetName(configSetName);
        final ConfigSetAdminResponse response = adminRequest.process(solrClient);
        SolrLogger.debug(this.getClass(), "Obtained response for 'deleteConfig': {}", response);
    }

    public String listConfig() throws SolrServerException, IOException {
        final ConfigSetAdminRequest.List adminRequest = new ConfigSetAdminRequest.List();
        final ConfigSetAdminResponse response = adminRequest.process(solrClient);
        SolrLogger.debug(this.getClass(), "Obtained response for 'listConfig': {}", response);
        return adminRequest.toString();
    }

    public void createCollection(String collectionName, String config) throws SolrServerException, IOException {
        final CollectionAdminRequest.Create creator = CollectionAdminRequest.createCollection(collectionName, config, shards, replicas);
        final CollectionAdminResponse response = creator.process(solrClient);
        SolrLogger.debug(this.getClass(), "Obtained response for 'deleteConfig': {}", response);
    }

    public void setCollectionProperty(String collectionName, String propertyName, String propertyValue) throws SolrServerException, IOException {
        final CollectionAdminRequest.CollectionProp property = CollectionAdminRequest.setCollectionProperty(collectionName, propertyName, propertyValue);
        final CollectionAdminResponse response = property.process(solrClient);
        SolrLogger.debug(this.getClass(), "Obtained response for 'deleteConfig': {}", response);
    }

    public void deleteCollection(String collectionName) throws SolrServerException, IOException {
        final CollectionAdminRequest.Delete eraser = CollectionAdminRequest.deleteCollection(collectionName);
        final CollectionAdminResponse response = eraser.process(solrClient);
        SolrLogger.debug(this.getClass(), "Obtained response for 'deleteConfig': {}", response);
    }

}
