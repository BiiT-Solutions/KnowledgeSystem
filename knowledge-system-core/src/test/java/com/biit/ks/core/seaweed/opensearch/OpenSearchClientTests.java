package com.biit.ks.core.seaweed.opensearch;

import com.biit.ks.core.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"opensearchClient"})
public class OpenSearchClientTests extends AbstractTestNGSpringContextTests {

    private static final String INDEX = "my-index";

    @Autowired
    private OpenSearchClient openSearchClient;


    @Test
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }

    @Test(dependsOnMethods = "createIndex")
    public void indexData() {
        final Data data = new Data("firstData", "The First Data");
        IndexResponse response = openSearchClient.indexData(data, INDEX, "1");
        Assert.assertEquals(response.id(), "1");
        Assert.assertEquals(response.result(), Result.Created);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchData() {
        final SearchResponse response = openSearchClient.searchData(Data.class, INDEX);
        //Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "searchData")
    public void deleteData() {
        final DeleteResponse response = openSearchClient.deleteData(INDEX, "1");
        Assert.assertEquals(response.id(), "1");
        Assert.assertEquals(response.result(), Result.Deleted);
    }

    @AfterClass
    public void cleanUp() {
        openSearchClient.deleteIndex(INDEX);
    }


    static class Data {
        private String name;
        private String description;

        public Data(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
