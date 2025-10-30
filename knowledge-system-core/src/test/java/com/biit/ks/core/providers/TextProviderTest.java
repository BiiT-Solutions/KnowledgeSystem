package com.biit.ks.core.providers;

/*-
 * #%L
 * Knowledge System (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
        Assert.assertFalse(textProvider.get(text.getUuid()).isEmpty());
    }


    @Test
    public void searchTextByContentAnyLanguage() {
        final SearchWrapper<Text> textEntries = textProvider.search("Lorem Donec sem tortor", 0, 100);
        Assert.assertEquals(textEntries.getTotalElements(), 1);
    }


    @Test
    public void searchTextByContentInLatin() {
        final SearchWrapper<Text> textEntries = textProvider.search("Lorem Donec sem tortor", TextLanguages.LA, 0, 100);
        Assert.assertEquals(textEntries.getTotalElements(), 1);
    }


    @Test
    public void searchTextByContentInSpanishWrong() {
        final SearchWrapper<Text> textEntries = textProvider.search("Lorem Donec sem tortor", TextLanguages.ES, 0, 100);
        Assert.assertEquals(textEntries.getTotalElements(), 0);
    }

    @Test
    public void searchTextByContentInSpanishCorrect() {
        final SearchWrapper<Text> textEntries = textProvider.search("cliente torturador", TextLanguages.ES, 0, 100);
        Assert.assertEquals(textEntries.getTotalElements(), 1);
    }


    @AfterClass(alwaysRun = true)
    public void deleteFiles() {
        textProvider.getAll(0, 100).forEach(fileEntry -> textProvider.delete(fileEntry));
        textProvider.delete(text);
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
    }
}
