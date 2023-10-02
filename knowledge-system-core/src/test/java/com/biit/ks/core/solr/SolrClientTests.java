package com.biit.ks.core.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootTest
@Test(groups = {"solrClients"})
public class SolrClientTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private SolrCollectionClient solrCollectionClient;

    @Autowired
    private SolrConfigClient solrConfigClient;

    @Autowired
    private SolrCoreClient solrCoreClient;

    @Autowired
    private SolrDocumentClient solrDocumentClient;

    @BeforeClass
    public static void coreGeneration() throws IOException, SolrServerException, URISyntaxException {

    }


    @Test
    public void addDocuments() throws IOException, SolrServerException {

    }
}
