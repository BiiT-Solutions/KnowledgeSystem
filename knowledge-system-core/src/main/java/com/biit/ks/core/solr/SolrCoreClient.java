package com.biit.ks.core.solr;

import com.biit.ks.logger.SolrLogger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SolrCoreClient extends SolrClient {

    public SolrCoreClient(@Value("${solr.server.url}") String serverUrl,
                          @Value("${solr.default.shards}") String shards,
                          @Value("${solr.default.replicas}") String replicas) {
        super(serverUrl, shards, replicas);
    }

    /**
     * Create a core where the indexed files will be stored.
     *
     * @param coreName    The name of the core.
     * @param instanceDir The directory to host the core. Must exist already and must have copied the content from 'solr/configsets/basic_configs'.
     * @param configFile  The file where the configuration of the core resides. The "solrconfig.xml"
     * @param schemaFile  The schema definition. The "schema.xml" file.
     * @throws SolrServerException
     * @throws IOException
     */
    public void createCore(String coreName, String instanceDir, String configFile, String schemaFile) throws SolrServerException, IOException {
        final CoreAdminResponse response = CoreAdminRequest.createCore(coreName, instanceDir, getSolrClient(), configFile, schemaFile);
        SolrLogger.debug(this.getClass(), "Obtained response for 'createCore': {}", response);
    }

    public void unloadCore(String coreName) throws SolrServerException, IOException {
        final CoreAdminResponse response = CoreAdminRequest.unloadCore(coreName, getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'unloadCore': {}", response);
    }

    public void createCoreSnapshot(String coreName, String snapshotName) throws SolrServerException, IOException {
        final CoreAdminRequest.CreateSnapshot createRequest = new CoreAdminRequest.CreateSnapshot(snapshotName);
        createRequest.setCoreName(coreName);
        final CoreAdminResponse response = createRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'createCoreBackup': {}", response);
    }

    public String listCoreSnapshot(String coreName) throws SolrServerException, IOException {
        final CoreAdminRequest.ListSnapshots listRequest = new CoreAdminRequest.ListSnapshots();
        listRequest.setCoreName(coreName);
        final CoreAdminResponse response = listRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'listCoreBackup': {}", response);
        return response.toString();
    }

    public void restoreCoreSnapshot(String coreName, String snapshotName) throws SolrServerException, IOException {
        throw new UnsupportedOperationException("No clue how to restore a snapshot yet!");
//        CoreAdminRequest.CreateSnapshot createRequest = new CoreAdminRequest.RequestRecovery();
//        createRequest.setCoreName(coreName);
//        CoreAdminResponse response = createRequest.process(getSolrClient());
//        SolrLogger.debug(this.getClass(), "Obtained response for 'restoreCoreBackup': {}", response);
    }
}
