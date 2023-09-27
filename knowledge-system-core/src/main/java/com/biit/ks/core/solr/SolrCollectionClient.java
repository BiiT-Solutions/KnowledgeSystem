package com.biit.ks.core.solr;

import com.biit.ks.logger.SolrLogger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class SolrCollectionClient extends SolrClient {

    public SolrCollectionClient(@Value("${solr.server.url}") String serverUrl,
                                @Value("${solr.default.shards}") String shards,
                                @Value("${solr.default.replicas}") String replicas) {
        super(serverUrl, shards, replicas);
    }

    /**
     * Create a backup. Repository name must exist already on the solr configuration file.
     * <a href="https://stackoverflow.com/questions/13947621/how-to-perform-a-remote-solr-core-backup-through-solrj-client">source</a>
     *
     * @param collectionName The collection to back up.
     * @param backupName     The name of the saved copy.
     * @param repositoryName The repo that has been defined on the solr configuration file.
     */
    public void createCollectionBackup(String collectionName, String backupName, String repositoryName) throws SolrServerException, IOException {
        final CollectionAdminRequest.Backup createRequest = CollectionAdminRequest.backupCollection(collectionName, backupName);
        createRequest.setRepositoryName(repositoryName);
        final CollectionAdminResponse response = createRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'createCollectionBackup': {}", response);
    }

    public String listCollectionBackup(String backupName) throws SolrServerException, IOException {
        final CollectionAdminRequest.ListBackup listRequest = CollectionAdminRequest.listBackup(backupName);
        final CollectionAdminResponse response = listRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'listCollectionBackup': {}", response);
        return response.toString();
    }

    public void restoreCollectionBackup(String collectionName, String backupName, String repositoryName) throws SolrServerException, IOException {
        final CollectionAdminRequest.Restore restoreRequest = CollectionAdminRequest.restoreCollection(collectionName, backupName);
        final CollectionAdminResponse response = restoreRequest.process(getSolrClient());
        SolrLogger.debug(this.getClass(), "Obtained response for 'restoreCollectionBackup': {}", response);
    }
}
