package com.biit.ks.core.controllers;

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

import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.core.providers.TextProvider;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"categorizationController"})
public class CategorizationControllerTest extends AbstractTestNGSpringContextTests {

    private final static String[] CATEGORIES = {"Category1", "Category2", "Category3", "Category4"};

    @Autowired
    private FileEntryProvider fileEntryProvider;

    @Autowired
    private TextProvider textProvider;

    @Autowired
    private CategorizationProvider categorizationProvider;

    @Autowired
    private CategorizationController categorizationController;

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private IOpenSearchConfigurator openSearchConfigurator;

    private final List<Categorization> categorizations = new ArrayList<>();

    @BeforeClass
    public void addCategories() {
        for (String category : CATEGORIES) {
            categorizations.add(categorizationProvider.save(new Categorization(category)));
        }
    }


    @BeforeClass(dependsOnMethods = "addCategories")
    public void addFiles() {
        final FileEntry entry = new FileEntry();
        entry.setName("FileEntry1");
        entry.setCategorizations(categorizations.subList(0, 1));
        fileEntryProvider.save(entry);
    }


    @BeforeClass(dependsOnMethods = "addCategories")
    public void addTexts() {
        final Text text = new Text();
        text.setName("Text1");
        text.setCategorizations(categorizations.subList(categorizations.size() - 2, categorizations.size() - 1));
        textProvider.save(text);
    }

    @Test
    public void checkCategorizationsStored() {
        Assert.assertEquals(categorizationController.getAll(0, 100).getTotalElements(), CATEGORIES.length);
    }


    @Test(dependsOnMethods = "checkCategorizationsStored")
    public void removeUnusedCategories() {
        categorizationController.deleteOrphanCategories();
        Assert.assertEquals(categorizationController.getAll(0, 100).getTotalElements(), 2);
    }


    @AfterClass(alwaysRun = true)
    public void deleteFiles() {
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchTextIndex());
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchCategorizationsIndex());
    }


}
