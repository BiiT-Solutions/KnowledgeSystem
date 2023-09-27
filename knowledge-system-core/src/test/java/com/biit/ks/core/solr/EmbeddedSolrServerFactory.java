package com.biit.ks.core.solr;


import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Not working
 */
public class EmbeddedSolrServerFactory {

    public static EmbeddedSolrServer create(String coreName) throws IOException, URISyntaxException {
        final Path solrHome = Files.createTempDirectory("solr");

        //Copy settings.
        FileUtils.copyDirectory(new File(EmbeddedSolrServerFactory.class.getResource("/solr").toURI()), solrHome.toFile());

        try (EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(solrHome, coreName)) {
            return embeddedSolrServer;
        }
    }
}
