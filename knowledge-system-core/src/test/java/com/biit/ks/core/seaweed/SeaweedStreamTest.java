package com.biit.ks.core.seaweed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import seaweedfs.client.FilerProto;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import static com.biit.ks.core.utils.Units.KB;
import static com.biit.ks.core.utils.Units.MB;

@SpringBootTest
@Test(groups = {"seaweedClient"})
public class SeaweedStreamTest extends AbstractTestNGSpringContextTests {
  private static final String SEAWEED_PATH = "/dir/withData";

  private static final String RESOURCE_FOLDER = "documents";
  private static final String RESOURCE = "irishJig";
  private static final String RESOURCE_EXTENSION = ".mp3";

  @Autowired
  private SeaweedClient seaweedClient;

  private long resourceSize;

  @Test
  public void addFile() throws IOException, URISyntaxException {
    File image = new File(getClass().getClassLoader().getResource(RESOURCE_FOLDER + File.separator + RESOURCE + RESOURCE_EXTENSION).toURI());
    resourceSize = Files.size(image.toPath());
    seaweedClient.addFile(SEAWEED_PATH + File.separator + RESOURCE, image);
  }

  @Test(dependsOnMethods = "addFile")
  public void getEntry() {
    final FilerProto.Entry entry = seaweedClient.getEntry(SEAWEED_PATH, RESOURCE);
    Assert.assertEquals(entry.getName(), RESOURCE);
    Assert.assertEquals(entry.getAttributes().getFileSize(), resourceSize);
  }

  @Test(dependsOnMethods = {"getEntry"}, alwaysRun = true)
  public void resumeStream() throws IOException {
    File result = File.createTempFile(RESOURCE, RESOURCE_EXTENSION);
    result.deleteOnExit();
    seaweedClient.saveChunk(SEAWEED_PATH + File.separator + RESOURCE, 100*KB, (int) (5*KB), result);
    Assert.assertEquals(Files.size(result.toPath()), 5*KB);
  }

  @Test(dependsOnMethods = {"resumeStream"}, alwaysRun = true)
  public void deleteFile() {
    seaweedClient.removeFile(SEAWEED_PATH + File.separator + RESOURCE);
    final List<FilerProto.Entry> entries = seaweedClient.listEntries(SEAWEED_PATH);
    Assert.assertEquals(entries.size(), 0);
  }
}
