package com.biit.ks.core.solr;

import com.biit.ks.core.solr.models.SolrDocument;
import com.biit.ks.logger.SolrLogger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SolrDocumentClient extends SolrClient {

    public SolrDocumentClient(@Value("${solr.server.url}") String serverUrl,
                              @Value("${solr.default.shards}") String shards,
                              @Value("${solr.default.replicas}") String replicas) {
        super(serverUrl, shards, replicas);
    }

    public SolrDocumentList searchDocuments(String coreName, Map<String, String> queryParamMap) throws SolrServerException, IOException {
        final MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        final QueryResponse response = getSolrClient().query(coreName, queryParams);
        SolrLogger.debug(this.getClass(), "Obtained response for 'searchDocuments': {}", response);
        return response.getResults();
    }

    public SolrDocumentList searchDocuments(String coreName, SolrQuery query) throws SolrServerException, IOException {
//        final SolrQuery query = new SolrQuery("*:*");
//        query.addField("id");
//        query.addField("name");
//        query.setSort("id", ORDER.asc);
//        query.setRows(numResultsToReturn);
        final QueryResponse response = getSolrClient().query(coreName, query);
        SolrLogger.debug(this.getClass(), "Obtained response for 'searchDocuments': {}", response);
        return response.getResults();
    }

    public void addDocument(String coreName, String name) throws SolrServerException, IOException {
        final SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", UUID.randomUUID().toString());
        doc.addField("name", name);

        final UpdateResponse response = getSolrClient().add(coreName, doc);
        // Indexed documents must be committed
        getSolrClient().commit(coreName);
        SolrLogger.debug(this.getClass(), "Obtained response for 'addDocument': {}", response);
    }

    public void addDocument(String coreName, SolrDocument document) throws SolrServerException, IOException {
        final UpdateResponse response = getSolrClient().addBean(coreName, document);
        // Indexed documents must be committed
        getSolrClient().commit(coreName);
        SolrLogger.debug(this.getClass(), "Obtained response for 'addDocument': {}", response);
    }

    public <T> List<T> searchCustomDocuments(String coreName, SolrQuery query, Class<T> beanType) throws SolrServerException, IOException {
        final QueryResponse response = getSolrClient().query(coreName, query);
        SolrLogger.debug(this.getClass(), "Obtained response for 'searchCustomDocuments': {}", response);
        return response.getBeans(beanType);
    }

}
