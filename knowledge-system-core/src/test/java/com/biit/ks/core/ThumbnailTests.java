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

import java.awt.image.BufferedImage;
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

    private static final String SEAWEED_PATH = "/testDir/withData";
    private static final String RESOURCE_FOLDER = "documents";
    private static final String RESOURCE = "1mb.mp4";

    private static final String TMPDIR = System.getProperty("java.io.tmpdir");

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
    public void createThumbnailFromVideoFromSeaweed() throws IOException {
        //Offset has a max of 16384 bytes
        final BufferedImage bufferedImage = thumbnailFactory.createThumbFromVideo(SEAWEED_PATH, RESOURCE);
        final byte[] image = thumbnailFactory.toByteArray(bufferedImage);
        Assert.assertNotNull(image);

        final File file = new File(TMPDIR + File.separator + "thumbnail-seaweed.png");
        file.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(image);
        }
    }

    @Test
    public void createThumbnailFromResource() throws IOException {
        final File source = new File(TMPDIR + File.separator + RESOURCE);
        source.deleteOnExit();
        Files.copy(getClass().getClassLoader().getResourceAsStream(RESOURCE_FOLDER + File.separator + RESOURCE),
                Paths.get(source.getPath()),
                StandardCopyOption.REPLACE_EXISTING);
        final byte[] image = thumbnailFactory.toByteArray(thumbnailFactory.createThumbFromVideo(TMPDIR + File.separator + RESOURCE));
        Assert.assertNotNull(image);

        final File file = new File(TMPDIR + File.separator + "thumbnail-resource.png");
        file.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(image);
        }
    }


    @Test(enabled = false)
    public void createThumbnailFromChunk() throws IOException {
        final byte[] chunk = seaweedClient.getBytes(SEAWEED_PATH + File.separator + RESOURCE, 0, 527868);
        final byte[] image = thumbnailFactory.toByteArray(thumbnailFactory.createThumbFromVideo(chunk));
        Assert.assertNotNull(image);

        final File file = new File(TMPDIR + File.separator + "thumbnail-chunk.png");
        file.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(file)) {
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
