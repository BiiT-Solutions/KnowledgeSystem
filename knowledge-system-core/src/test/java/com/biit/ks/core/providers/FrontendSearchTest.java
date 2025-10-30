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

import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
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
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"fileEntryProvider"})
public class FrontendSearchTest extends AbstractTestNGSpringContextTests {

    private final static String[] CATEGORIES = {"Category1", "Category2", "Category3", "Category4"};

    @Autowired
    private FileEntryProvider fileEntryProvider;

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private IOpenSearchConfigurator openSearchConfigurator;

    @Autowired
    private CategorizationProvider categorizationProvider;

    private final List<Categorization> categorizations = new ArrayList<>();


    private FileEntry fileEntry1;
    private FileEntry fileEntry2;
    private FileEntry fileEntry3;
    private FileEntry fileEntry4;

    @BeforeClass
    public void addCategories() {
        for (String category : CATEGORIES) {
            categorizations.add(categorizationProvider.save(new Categorization(category)));
        }
    }


    @BeforeClass(dependsOnMethods = "addCategories")
    public void init() {
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.createIndex(openSearchConfigurator.getOpenSearchFileIndex());

        fileEntry1 = new FileEntry();
        fileEntry1.setFileName("File1");
        fileEntry1.setAlias("FileTest");
        fileEntry1.setThumbnailUrl(null);
        fileEntry1.setCreatedAt(LocalDateTime.now().minusMinutes(20));
        fileEntry1.setCategorizations(List.of(categorizations.get(0)));
        fileEntryProvider.save(fileEntry1);

        fileEntry2 = new FileEntry();
        fileEntry2.setFileName("File2");
        fileEntry2.setAlias("FileTest");
        fileEntry2.setThumbnailUrl("/testurl");
        fileEntry2.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        fileEntry2.setCategorizations(List.of(categorizations.get(0), categorizations.get(1)));
        fileEntryProvider.save(fileEntry2);

        fileEntry3 = new FileEntry();
        fileEntry3.setFileName("File3");
        fileEntry3.setAlias("FileTest");
        fileEntry3.setThumbnailUrl(null);
        fileEntry3.setCreatedAt(LocalDateTime.now());
        fileEntry3.setCategorizations(List.of(categorizations.get(0), categorizations.get(1), categorizations.get(2)));
        fileEntryProvider.save(fileEntry3);

        fileEntry4 = new FileEntry();
        fileEntry4.setFileName("File4");
        fileEntry4.setAlias("FileTest");
        fileEntry4.setThumbnailUrl("/testurl");
        fileEntry4.setCreatedAt(LocalDateTime.now().plusMinutes(5));
        fileEntry4.setCategorizations(new ArrayList<>());
        fileEntryProvider.save(fileEntry4);
    }

    @Test
    public void searchByContent() {
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("FileTest", null, null, null, null, null), 0, 100).getTotalElements(), 4);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("FilaTast", null, null, null, null, null), 0, 100).getTotalElements(), 4);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("FullTast", null, null, null, null, null), 0, 100).getTotalElements(), 0);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("File", null, null, null, null, null), 0, 100).getTotalElements(), 4);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("Fila3", null, null, null, null, null), 0, 100).getTotalElements(), 1);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch("leTest", null, null, null, null, null), 0, 100).getTotalElements(), 4);
    }

    @Test
    public void searchByDate() {
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, LocalDateTime.now().minusMinutes(30), null, null), 0, 100).getTotalElements(), 4);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, LocalDateTime.now().minusMinutes(15), null, null), 0, 100).getTotalElements(), 3);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, LocalDateTime.now().minusMinutes(6), null, null), 0, 100).getTotalElements(), 2);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, LocalDateTime.now().plusMinutes(5), null, null), 0, 100).getTotalElements(), 0);

        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, LocalDateTime.now().minusMinutes(30), LocalDateTime.now(), null), 0, 100).getTotalElements(), 3);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, LocalDateTime.now().minusMinutes(30), LocalDateTime.now().minusMinutes(11), null), 0, 100).getTotalElements(), 1);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, LocalDateTime.now().minusMinutes(30), LocalDateTime.now().minusMinutes(1), null), 0, 100).getTotalElements(), 2);
    }


    @Test
    public void searchByKeywords() {
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, null, null, List.of("Category1")), 0, 100).getTotalElements(), 3);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, null, null, List.of("Category2")), 0, 100).getTotalElements(), 2);
        Assert.assertEquals(fileEntryProvider.search(new SimpleSearch(null, null, null, null, null, List.of("Category2", "Category3")), 0, 100).getTotalElements(), 1);
    }


    @AfterClass(alwaysRun = true)
    public void deleteFiles() throws InterruptedException {

        fileEntryProvider.getAll(0, 100).forEach(fileEntry -> fileEntryProvider.delete(fileEntry));

        fileEntryProvider.delete(fileEntry1);
        fileEntryProvider.delete(fileEntry2);
        fileEntryProvider.delete(fileEntry3);
        fileEntryProvider.delete(fileEntry4);

        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchTextIndex());
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchCategorizationsIndex());
    }
}
