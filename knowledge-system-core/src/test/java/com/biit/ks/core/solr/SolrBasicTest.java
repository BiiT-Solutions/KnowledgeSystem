package com.biit.ks.core.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@Test
public class SolrBasicTest {

    static EmbeddedSolrServer solrClient;

    @BeforeClass
    public static void setupClass() throws IOException, SolrServerException, URISyntaxException {
        solrClient = EmbeddedSolrServerFactory.create("exampleCollection");

        // create some test documents
        SolrInputDocument doc1 = new SolrInputDocument();
        doc1.addField("id", "1");

        SolrInputDocument doc2 = new SolrInputDocument();
        doc2.addField("id", "2");

        SolrInputDocument doc3 = new SolrInputDocument();
        doc3.addField("id", "3");

        SolrInputDocument doc4 = new SolrInputDocument();
        doc4.addField("id", "4");

        SolrInputDocument doc5 = new SolrInputDocument();
        doc5.addField("id", "5");

        // add the test data to the index
        solrClient.add(Arrays.asList(doc1, doc2, doc3, doc4, doc5));
        solrClient.commit();
    }

    @AfterClass
    public static void teardownClass() {
        try {
            solrClient.close();
        } catch (Exception e) {
        }
    }

    @Test
    public void testEmbeddedSolrServerFactory() throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery("*:*");
        QueryResponse response = solrClient.query(solrQuery);
        Assert.assertNotNull(response);

        SolrDocumentList solrDocuments = response.getResults();
        Assert.assertNotNull(solrDocuments);
        Assert.assertEquals(5, solrDocuments.getNumFound());
    }
}
