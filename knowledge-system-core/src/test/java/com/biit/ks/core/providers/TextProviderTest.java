package com.biit.ks.core.providers;


import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@SpringBootTest
@Test(groups = {"fileEntryProvider"})
public class TextProviderTest extends AbstractTestNGSpringContextTests {

    private final static String TEXT_LA = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tortor sem, pharetra vel ornare quis, cursus sed nibh.";
    private final static String TEXT_ES = "El cliente es muy importante, el cliente será seguido por el cliente. Hasta el torturador, la aljaba o el adorno, el curso pero el nibh.";

    @Autowired
    private TextProvider textProvider;

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private IOpenSearchConfigurator openSearchConfigurator;

    private Text text;


    @BeforeClass
    public void init() {
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.createIndex(openSearchConfigurator.getOpenSearchFileIndex());

        text = new Text();
        text.setName("test");
        text.setDescription("description");
        text.addContent(TextLanguages.LA, TEXT_LA);
        text.addContent(TextLanguages.ES, TEXT_ES);
        textProvider.save(text);
    }

    @Test
    public void searchTextByUUID() {
        Assert.assertTrue(textProvider.get(text.getUuid()).isPresent());
    }


    @Test
    public void searchTextByContentAnyLanguage() {
        final List<Text> textEntries = textProvider.search("Lorem Donec sem tortor", 0, 100);
        Assert.assertEquals(textEntries.size(), 1);
    }


    @Test
    public void searchTextByContentInLatin() {
        final List<Text> textEntries = textProvider.search("Lorem Donec sem tortor", TextLanguages.LA, 0, 100);
        Assert.assertEquals(textEntries.size(), 1);
    }


    @Test
    public void searchTextByContentInSpanishWrong() {
        final List<Text> textEntries = textProvider.search("Lorem Donec sem tortor", TextLanguages.ES, 0, 100);
        Assert.assertEquals(textEntries.size(), 0);
    }

    @Test
    public void searchTextByContentInSpanishCorrect() {
        final List<Text> textEntries = textProvider.search("cliente torturador", TextLanguages.ES, 0, 100);
        Assert.assertEquals(textEntries.size(), 1);
    }


    @AfterClass(alwaysRun = true)
    public void deleteFiles() throws InterruptedException {
        textProvider.getAll(0, 100).forEach(fileEntry -> textProvider.delete(fileEntry));
        textProvider.delete(text);
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
    }
}
