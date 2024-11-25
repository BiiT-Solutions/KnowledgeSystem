package com.biit.ks.core.seaweed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import seaweedfs.client.FilerProto;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

@SpringBootTest
@Test(groups = {"seaweedClient"})
public class SeaweedClientTests extends AbstractTestNGSpringContextTests {

    private static final String RESOURCE_FOLDER = "documents";
    private static final String RESOURCE = "Chuthulu";
    private static final String RESOURCE_EXTENSION = ".png";

    @Autowired
    private SeaweedClient seaweedClient;

    @Autowired
    private SeaweedConfigurator seaweedConfigurator;

    private long resourceSize;

    @BeforeClass
    public void createFolder() {
        seaweedClient.createFolder(seaweedConfigurator.getUploadsPath(), 0755);
    }

    @Test
    public void addFile() throws IOException, URISyntaxException {
        File image = new File(getClass().getClassLoader().getResource(RESOURCE_FOLDER + File.separator + RESOURCE + RESOURCE_EXTENSION).toURI());
        resourceSize = Files.size(image.toPath());
        seaweedClient.addFile(seaweedConfigurator.getUploadsPath() + File.separator + RESOURCE, image);
    }

    @Test(dependsOnMethods = "addFile")
    public void downloadFile() throws IOException {
        File result = File.createTempFile(RESOURCE, RESOURCE_EXTENSION);
        result.deleteOnExit();
        seaweedClient.getFile(seaweedConfigurator.getUploadsPath() + File.separator + RESOURCE, result);
    }

    @Test(dependsOnMethods = "addFile")
    public void listFiles() {
        final List<FilerProto.Entry> entries = seaweedClient.listEntries(seaweedConfigurator.getUploadsPath());
        Assert.assertEquals(entries.size(), 1);
    }

    @Test(dependsOnMethods = "addFile")
    public void getEntry() {
        final FilerProto.Entry entry = seaweedClient.getEntry(seaweedConfigurator.getUploadsPath(), RESOURCE);
        Assert.assertEquals(entry.getName(), RESOURCE);
        Assert.assertEquals(entry.getAttributes().getFileSize(), resourceSize);
    }

    @Test(dependsOnMethods = {"downloadFile", "listFiles", "getEntry"}, alwaysRun = true)
    public void deleteFile() {
        seaweedClient.removeFile(seaweedConfigurator.getUploadsPath() + File.separator + RESOURCE);

        final List<FilerProto.Entry> entries = seaweedClient.listEntries(seaweedConfigurator.getUploadsPath());
        Assert.assertEquals(entries.size(), 0);
    }

    @AfterClass(alwaysRun = true)
    public void deleteFolder() {
        //seaweedClient.deleteFolder(seaweedConfigurator.getUploadsPath());
        seaweedClient.wipeOut();
    }

}
