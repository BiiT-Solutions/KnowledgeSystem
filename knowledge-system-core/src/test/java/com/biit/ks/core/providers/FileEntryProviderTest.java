package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

@SpringBootTest
@Test(groups = {"fileEntryProvider"})
public class FileEntryProviderTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private FileEntryProvider fileEntryProvider;

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private IOpenSearchConfigurator openSearchConfigurator;


    private FileEntry fileEntry1;
    private FileEntry fileEntry2;
    private FileEntry fileEntry3;
    private FileEntry fileEntry4;


    @BeforeClass
    public void init() {
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.createIndex(openSearchConfigurator.getOpenSearchFileIndex());

        fileEntry1 = new FileEntry();
        fileEntry1.setFileName("File1");
        fileEntry1.setAlias("FileTest");
        fileEntry1.setThumbnailUrl(null);
        fileEntry1.setCreatedAt(LocalDateTime.now().minusMinutes(120));
        fileEntryProvider.save(fileEntry1);

        fileEntry2 = new FileEntry();
        fileEntry2.setFileName("File2");
        fileEntry2.setAlias("FileTest");
        fileEntry2.setThumbnailUrl("/testurl");
        fileEntry2.setCreatedAt(LocalDateTime.now().minusMinutes(60));
        fileEntryProvider.save(fileEntry2);

        fileEntry3 = new FileEntry();
        fileEntry3.setFileName("File3");
        fileEntry3.setAlias("FileTest");
        fileEntry3.setThumbnailUrl(null);
        fileEntry3.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        fileEntryProvider.save(fileEntry3);

        fileEntry4 = new FileEntry();
        fileEntry4.setFileName("File4");
        fileEntry4.setAlias("FileTest");
        fileEntry4.setThumbnailUrl("/testurl");
        fileEntry4.setCreatedAt(LocalDateTime.now());
        fileEntryProvider.save(fileEntry4);
    }


    @Test
    public void searchFileEntryByName() {
        final SearchWrapper<FileEntry> fileEntries = fileEntryProvider.findByAlias("FileTest", 0, 10);
        Assert.assertEquals(fileEntries.getTotalElements(), 4);
    }


    @Test
    public void searchFileEntryWithoutThumbnails() {
        final SearchWrapper<FileEntry> fileEntries = fileEntryProvider.findFilesWithoutThumbnail();
        Assert.assertEquals(fileEntries.getTotalElements(), 2);
    }

    @Test
    public void searchFileBySimpleSearch() {
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("FileTest", null, null, null, null, null), 0, 100).getTotalElements(), 4);
    }

    @Test
    public void searchFileBySimpleSearchOneValue() {
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("FileTest", null, null, null, null, null), 0, 1).getData().size(), 1);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("FileTest", null, null, null, null, null), 0, 1).getTotalElements(), 4);
    }

    @Test
    public void searchFileEntryByDate() {
        ShouldHavePredicates search = new ShouldHavePredicates();
        search.addRange("createdAt", LocalDateTime.now().minusMinutes(200), null);
        SearchWrapper<FileEntry> fileEntries = fileEntryProvider.search(search);
        Assert.assertEquals(fileEntries.getTotalElements(), 4);

        search = new ShouldHavePredicates();
        search.addRange("createdAt", LocalDateTime.now().minusMinutes(65), null);
        fileEntries = fileEntryProvider.search(search);
        Assert.assertEquals(fileEntries.getTotalElements(), 3);

        search = new ShouldHavePredicates();
        search.addRange("createdAt", LocalDateTime.now().minusMinutes(1), null);
        fileEntries = fileEntryProvider.search(search);
        Assert.assertEquals(fileEntries.getTotalElements(), 1);

        search = new ShouldHavePredicates();
        search.addRange("createdAt", LocalDateTime.now().plusMinutes(1), null);
        fileEntries = fileEntryProvider.search(search);
        Assert.assertEquals(fileEntries.getTotalElements(), 0);
    }


    @AfterClass(alwaysRun = true)
    public void deleteFiles() {

        fileEntryProvider.getAll(0, 100).getData().forEach(fileEntry -> fileEntryProvider.delete(fileEntry));

        fileEntryProvider.delete(fileEntry1);
        fileEntryProvider.delete(fileEntry2);
        fileEntryProvider.delete(fileEntry3);
        fileEntryProvider.delete(fileEntry4);

        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
    }

}
