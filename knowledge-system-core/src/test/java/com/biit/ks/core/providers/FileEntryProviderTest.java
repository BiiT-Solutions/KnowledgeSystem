package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import com.biit.ks.persistence.repositories.OpenSearchConfigurator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
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
        fileEntry1.setThumbnail(null);
        fileEntryProvider.save(fileEntry1);

        fileEntry2 = new FileEntry();
        fileEntry2.setFileName("File2");
        fileEntry2.setAlias("FileTest");
        fileEntry2.setThumbnail(new byte[0]);
        fileEntryProvider.save(fileEntry2);

        fileEntry3 = new FileEntry();
        fileEntry3.setFileName("File3");
        fileEntry3.setAlias("FileTest");
        fileEntry3.setThumbnail(null);
        fileEntryProvider.save(fileEntry3);

        fileEntry4 = new FileEntry();
        fileEntry4.setFileName("File4");
        fileEntry4.setAlias("FileTest");
        fileEntry4.setThumbnail(new byte[0]);
        fileEntryProvider.save(fileEntry4);
    }


    @Test
    public void searchFileEntryWithoutThumbnails() {
        final List<FileEntry> fileEntries = fileEntryProvider.findFilesWithoutThumbnail();
        Assert.assertEquals(fileEntries.size(), 2);
    }

    @AfterClass(alwaysRun = true)
    public void deleteFiles() throws InterruptedException {

        fileEntryProvider.getAll(0, 100).forEach(fileEntry -> fileEntryProvider.delete(fileEntry));

        fileEntryProvider.delete(fileEntry1);
        fileEntryProvider.delete(fileEntry2);
        fileEntryProvider.delete(fileEntry3);
        fileEntryProvider.delete(fileEntry4);

        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
    }

}
