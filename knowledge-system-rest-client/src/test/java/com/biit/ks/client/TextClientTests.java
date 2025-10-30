package com.biit.ks.client;

/*-
 * #%L
 * Knowledge System (Rest Client)
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


import com.biit.ks.TestKnowledgeSystemServer;
import com.biit.ks.core.providers.TextProvider;
import com.biit.ks.dto.TextDTO;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TestKnowledgeSystemServer.class)
@Test(groups = {"textClientTests"})
public class TextClientTests extends AbstractTestNGSpringContextTests {

    private final static String TEXT_NAME = "Text Name";
    private final static String TEXT_LA = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tortor sem, pharetra vel ornare quis, cursus sed nibh.";
    private final static String TEXT_ES = "El cliente es muy importante, el cliente será seguido por el cliente. Hasta el torturador, la aljaba o el adorno, el curso pero el nibh.";

    private final static String USER_NAME = "user";
    private final static String USER_PASSWORD = "password";

    @Autowired
    private TextProvider textProvider;

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private IOpenSearchConfigurator openSearchConfigurator;

    @Autowired
    private TextClient textClient;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    private Text text;


    @BeforeClass
    public void addText() {
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.createIndex(openSearchConfigurator.getOpenSearchFileIndex());

        Text text = new Text();
        text.setName(TEXT_NAME);
        text.setDescription("description");
        text.addContent(TextLanguages.LA, TEXT_LA);
        text.addContent(TextLanguages.ES, TEXT_ES);
        this.text = textProvider.save(text);
    }

    @BeforeClass
    public void addUser() {
        //Create the admin user
        authenticatedUserProvider.createUser(USER_NAME, USER_NAME, USER_PASSWORD);
    }

    @Test
    public void findTextByUUID() {
        final Optional<TextDTO> retrievedText = textClient.get(text.getUuid());
        Assert.assertTrue(retrievedText.isPresent());
        Assert.assertEquals(retrievedText.get().getName(), TEXT_NAME);
    }

    @Test
    public void findTextByUUIDAndLatin() {
        final Optional<String> retrievedText = textClient.get(text.getUuid(), TextLanguages.LA.name());
        Assert.assertTrue(retrievedText.isPresent());
        Assert.assertEquals(retrievedText.get(), TEXT_LA);
    }

    @Test
    public void findTextByUUIDAndSpanish() {
        final Optional<String> retrievedText = textClient.get(text.getUuid(), TextLanguages.ES.name());
        Assert.assertTrue(retrievedText.isPresent());
        Assert.assertEquals(retrievedText.get(), TEXT_ES);
    }

    @Test
    public void findTextByNameAndLatin() {
        final Optional<String> retrievedText = textClient.get(TEXT_NAME, TextLanguages.LA.name());
        Assert.assertTrue(retrievedText.isPresent());
        Assert.assertEquals(retrievedText.get(), TEXT_LA);
    }

    @Test
    public void findTextByNameAndSpanish() {
        final Optional<String> retrievedText = textClient.get(TEXT_NAME, TextLanguages.ES.name());
        Assert.assertTrue(retrievedText.isPresent());
        Assert.assertEquals(retrievedText.get(), TEXT_ES);
    }
}
