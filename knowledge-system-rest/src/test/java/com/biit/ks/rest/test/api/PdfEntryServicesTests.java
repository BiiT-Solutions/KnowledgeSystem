package com.biit.ks.rest.test.api;

import com.biit.ks.dto.FileEntryDTO;
import com.biit.ks.core.seaweed.SeaweedClient;
import com.biit.ks.core.seaweed.SeaweedConfigurator;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import com.biit.server.security.model.AuthRequest;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@Test(groups = "pdfEntry")
public class PdfEntryServicesTests extends AbstractTestNGSpringContextTests {

    private final static String USER_NAME = "user";
    private final static String USER_PASSWORD = "password";

    private final static String JWT_SALT = "4567";

    private final static String PDF_FILE = "Cookies.pdf";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OpenSearchClient openSearchClient;

    @Autowired
    private IOpenSearchConfigurator openSearchConfigurator;

    @Autowired
    private SeaweedConfigurator seaweedConfigurator;

    @Autowired
    private SeaweedClient seaweedClient;

    private MockMvc mockMvc;

    private String jwtToken;

    private UUID pdfUUID;
    private UUID imageUUID;

    private <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }


    private <T> T fromJson(String payload, Class<T> clazz) throws IOException {
        return objectMapper.readValue(payload, clazz);
    }


    @BeforeClass
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    @BeforeClass(dependsOnMethods = "setUp")
    public void addUser() {
        //Create the admin user
        authenticatedUserProvider.createUser(USER_NAME, USER_NAME, USER_PASSWORD);
    }


    @BeforeClass(dependsOnMethods = "addUser")
    public void checkAuthentication() {
        //Check the admin user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USER_NAME, JWT_SALT + USER_PASSWORD));
    }


    @BeforeClass(dependsOnMethods = "addUser")
    public void setAuthentication() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(USER_NAME);
        request.setPassword(USER_PASSWORD);

        MvcResult createResult = this.mockMvc
                .perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                .andReturn();

        jwtToken = createResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        Assert.assertNotNull(jwtToken);
    }

    @BeforeClass
    public void prepareIndexes() {
        openSearchClient.createIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.createIndex(openSearchConfigurator.getOpenSearchCategorizationsIndex());
    }

    @Test
    public void uploadPdf() throws Exception {
        //Must be called "file" to match the MultipartFile parameter name.
        final MockMultipartFile multipartFile = new MockMultipartFile("file", "myPdf", null,
                PdfEntryServicesTests.class.getClassLoader().getResourceAsStream(PDF_FILE));

        MvcResult createResult = this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/files/upload")
                        .file(multipartFile)
                        .param("force", "true")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final FileEntryDTO fileEntryResult = fromJson(createResult.getResponse().getContentAsString(), FileEntryDTO.class);
        Assert.assertEquals(fileEntryResult.getMimeType(), "application/pdf");
        pdfUUID = fileEntryResult.getUuid();
    }


    @Test(dependsOnMethods = "uploadPdf")
    public void searchPdf() throws Exception {
        MvcResult createResult = this.mockMvc
                .perform(get("/files/search/query:myPdf")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<FileEntryDTO> fileEntryResults = Arrays.asList(fromJson(createResult.getResponse().getContentAsString(), FileEntryDTO[].class));
        Assert.assertEquals(fileEntryResults.size(), 1);
    }


    @Test(dependsOnMethods = "uploadPdf")
    public void getAll() throws Exception {
        Thread.sleep(5000);
        MvcResult createResult = this.mockMvc
                .perform(get("/files")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<FileEntryDTO> fileEntryResults = Arrays.asList(fromJson(createResult.getResponse().getContentAsString(), FileEntryDTO[].class));
        Assert.assertEquals(fileEntryResults.size(), 1);
    }

    @Test(dependsOnMethods = {"searchPdf", "getAll"})
    public void checkThumbnail() throws Exception {
        Thread.sleep(5000);

        MvcResult createResult = this.mockMvc
                .perform(get("/thumbnails/public/downloads/" + pdfUUID.toString())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final byte[] thumbnail = createResult.getResponse().getContentAsByteArray();
        Assert.assertNotNull(thumbnail);

        final File file = new File("/tmp/thumbnail-pdf.png");
        file.deleteOnExit();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(thumbnail);
        }
        Assert.assertTrue(file.length() > 0);
    }


    @Test(dependsOnMethods = {"searchPdf", "getAll"}, alwaysRun = true)
    public void deletePdf() throws Exception {
        this.mockMvc
                .perform(delete("/files/" + pdfUUID.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
    }


    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchFileIndex());
        openSearchClient.deleteIndex(openSearchConfigurator.getOpenSearchCategorizationsIndex());
    }


    @AfterClass(alwaysRun = true)
    public void deleteFolder() {
        seaweedClient.deleteFolder(seaweedConfigurator.getUploadsPath());
        seaweedClient.deleteFolder(seaweedConfigurator.getThumbnailsPath());
    }

    @BeforeClass(alwaysRun = true)
    public void deleteUser() {
        //Create the admin user
        authenticatedUserProvider.deleteUser(USER_NAME, USER_NAME);
    }
}
