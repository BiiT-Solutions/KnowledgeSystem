package com.biit.ks.core.solr;

import com.biit.ks.logger.SolrLogger;
import org.apache.solr.client.solrj.impl.Http2SolrClient;

import java.util.concurrent.TimeUnit;

public abstract class SolrClient {
    private static final int CLIENT_CONNECTION_TIMEOUT_SECONDS = 10;
    private final Http2SolrClient solrClient;
    private final Integer shards;
    private final Integer replicas;


    public SolrClient(String serverUrl, String shards, String replicas) {
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

}
