package com.biit.ks.core.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"opensearchClient"})
public class OpenSearchClientTests extends AbstractTestNGSpringContextTests {

    private static final String INDEX = "sample-index";
    private static final String DATA_ID = "1";
    private static final String DATA_NAME = "firstData";
    private static final String DATA_DESCRIPTION = "The First Data";

    @Autowired
    private OpenSearchClient openSearchClient;


    @Test
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }


    @Test(dependsOnMethods = "createIndex")
    public void indexData() {
        final Data data = new Data(DATA_NAME, DATA_DESCRIPTION);
        IndexResponse response = openSearchClient.indexData(data, INDEX, DATA_ID);
        Assert.assertEquals(response.index(), INDEX);
        Assert.assertEquals(response.id(), DATA_ID);
        Assert.assertEquals(response.result(), Result.Created);
        openSearchClient.refreshIndex();
    }


    @Test(dependsOnMethods = "indexData")
    public void getData() {
        final GetResponse<Data> response = openSearchClient.getData(Data.class, INDEX, DATA_ID);
        Assert.assertEquals(response.id(), DATA_ID);
        Assert.assertEquals(response.source().getName(), DATA_NAME);
        Assert.assertEquals(response.source().getDescription(), DATA_DESCRIPTION);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchData() {
        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, INDEX);
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataByFieldExists() {
        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, new ExistsQuery.Builder().field("description").build()._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataByFieldNotExists() {
        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, new ExistsQuery.Builder().field("descriptions").build()._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 0);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataByQuery() {
        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, new MatchQuery.Builder().field("description")
                .query(FieldValue.of(DATA_DESCRIPTION)).build()._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataByShouldQuery() {
        final List<Pair<String, String>> shouldParameters = new ArrayList<>();
        shouldParameters.add(Pair.of("name", DATA_NAME));
        shouldParameters.add(Pair.of("name", "wrong"));

        final SearchResponse<Data> response = openSearchClient.searchDataShould(Data.class, shouldParameters, 1);
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataByShouldQueryInvalid() {
        final List<Pair<String, String>> shouldParameters = new ArrayList<>();
        shouldParameters.add(Pair.of("name", DATA_NAME));
        shouldParameters.add(Pair.of("name", "wrong"));
        shouldParameters.add(Pair.of("name", "wrong2"));

        final SearchResponse<Data> response = openSearchClient.searchDataShould(Data.class, shouldParameters, 2);
        Assert.assertEquals(response.hits().hits().size(), 0);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataByMustNotQuery() {
        final List<Pair<String, String>> mustNotHaveParameters = new ArrayList<>();
        mustNotHaveParameters.add(Pair.of("name", DATA_NAME));

        final SearchResponse<Data> response = openSearchClient.searchDataMustNot(Data.class, mustNotHaveParameters);
        for (Hit<Data> data : response.hits().hits()) {
            Assert.assertNotNull(data.source(), DATA_NAME);
            Assert.assertNotEquals(data.source().getName(), DATA_NAME);
        }
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataByMustQuery() {
        final List<Pair<String, String>> mustHaveParameters = new ArrayList<>();
        mustHaveParameters.add(Pair.of("name", DATA_NAME));
        mustHaveParameters.add(Pair.of("name", "wrong"));

        final SearchResponse<Data> response = openSearchClient.searchDataMust(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 0);
    }

    @Test(dependsOnMethods = {"searchData", "getData", "searchDataByQuery", "searchDataByShouldQuery", "searchDataByMustQuery", "searchDataByShouldQueryInvalid", "searchDataByMustNotQuery"}, alwaysRun = true)
    public void deleteData() {
        final DeleteResponse response = openSearchClient.deleteData(INDEX, DATA_ID);
        Assert.assertEquals(response.id(), DATA_ID);
        Assert.assertEquals(response.result(), Result.Deleted);
    }

    @AfterClass
    public void cleanUp() {
        openSearchClient.deleteIndex(INDEX);
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Data {
        private String name;
        private String description;

        public Data() {
        }

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
