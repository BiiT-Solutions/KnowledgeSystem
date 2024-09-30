package com.biit.ks.core;

import com.biit.ks.core.files.ThumbnailFactory;
import com.biit.ks.core.seaweed.SeaweedClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import seaweedfs.client.FilerProto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@SpringBootTest
@Test(groups = {"thumbnailTests"})
public class ThumbnailTests extends AbstractTestNGSpringContextTests {

    private static final String SEAWEED_PATH = "/dir/withData";
    private static final String RESOURCE_FOLDER = "documents";
    private static final String RESOURCE = "1mb.mp4";

    private static String tmpdir = System.getProperty("java.io.tmpdir");
    ;

    @Autowired
    private SeaweedClient seaweedClient;

    @Autowired
    private ThumbnailFactory thumbnailFactory;

    @BeforeClass

    public void createFolder() {
        seaweedClient.createFolder(SEAWEED_PATH, 0755);
    }

    @BeforeClass
    public void addFile() throws IOException, URISyntaxException {
        File video = new File(getClass().getClassLoader().getResource(RESOURCE_FOLDER + File.separator + RESOURCE).toURI());
        seaweedClient.addFile(SEAWEED_PATH + File.separator + RESOURCE, video);
    }

    @Test
    public void createThumbnailFromVideo() throws IOException {
        final byte[] image = thumbnailFactory.toByteArray(thumbnailFactory.createThumbFromVideo(seaweedClient
                .getBytes(SEAWEED_PATH, RESOURCE)));
        Assert.assertNotNull(image);

        try (FileOutputStream fos = new FileOutputStream(tmpdir + File.separator + "thumbnail.png")) {
            fos.write(image);
        }
    }

    @Test
    public void createThumbnailFromResource() throws IOException {
        Files.copy(getClass().getClassLoader().getResourceAsStream(RESOURCE_FOLDER + File.separator + RESOURCE),
                Paths.get("/tmp" + File.separator + RESOURCE),
                StandardCopyOption.REPLACE_EXISTING);
        final byte[] image = thumbnailFactory.toByteArray(thumbnailFactory.createThumbFromVideo(tmpdir + File.separator + RESOURCE));
        Assert.assertNotNull(image);

        try (FileOutputStream fos = new FileOutputStream(tmpdir + File.separator + "thumbnail.png")) {
            fos.write(image);
        }
    }


    @Test
    public void createThumbnailFromChunk() throws IOException {
        final byte[] image = thumbnailFactory.toByteArray(thumbnailFactory.createThumbFromVideo(seaweedClient
                .getBytes(SEAWEED_PATH + File.separator + RESOURCE, 52400, 102400)));
        Assert.assertNotNull(image);

        try (FileOutputStream fos = new FileOutputStream(tmpdir + File.separator + "thumbnail.png")) {
            fos.write(image);
        }
    }

    @AfterClass(alwaysRun = true)
    public void deleteFile() {
        seaweedClient.removeFile(SEAWEED_PATH + File.separator + RESOURCE);
        final List<FilerProto.Entry> entries = seaweedClient.listEntries(SEAWEED_PATH);
        Assert.assertEquals(entries.size(), 0);
    }
}
