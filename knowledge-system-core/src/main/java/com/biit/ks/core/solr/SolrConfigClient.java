package com.biit.ks.core.solr;

import com.biit.ks.logger.SolrLogger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class SolrConfigClient extends SolrClient {

    public SolrConfigClient(@Value("${solr.server.url}") String serverUrl,
                            @Value("${solr.default.shards}") String shards,
                            @Value("${solr.default.replicas}") String replicas) {
        super(serverUrl, shards, replicas);
    }

    public void createConfigSet(String configSetName, String baseConfigSetName) throws SolrServerException, IOException {
        final ConfigSetAdminRequest.Create createRequest = new ConfigSetAdminRequest.Create();
        createRequest.setConfigSetName(configSetName);
        createRequest.setBaseConfigSetName(baseConfigSetName);
        final ConfigSetAdminResponse response = createRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'createConfigSet': {}", response);
    }

    public void deleteConfigSet(String configSetName) throws SolrServerException, IOException {
        final ConfigSetAdminRequest.Delete deleteRequest = new ConfigSetAdminRequest.Delete();
        deleteRequest.setConfigSetName(configSetName);
        final ConfigSetAdminResponse response = deleteRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'deleteConfigSet': {}", response);
    }

    public String listConfigSet(String backupName) throws SolrServerException, IOException {
        final ConfigSetAdminRequest.List listRequest = new ConfigSetAdminRequest.List();
        final ConfigSetAdminResponse.List response = listRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'listConfigSet': {}", response);
        return response.toString();
    }
}
