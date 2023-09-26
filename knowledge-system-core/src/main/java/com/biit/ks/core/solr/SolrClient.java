package com.biit.ks.core.solr;

import com.biit.ks.logger.SolrLogger;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolrClient {
    final Http2SolrClient solrClient;


    public SolrClient(@Value("${solr.server.url}") String serverUrl) {
        SolrLogger.debug(this.getClass(), "Using url '{}'", serverUrl);
        this.solrClient = new Http2SolrClient.Builder(serverUrl).build();
    }


}