package com.biit.ks.core.seaweed.opensearch;

import com.biit.ks.core.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    public void indexData() throws InterruptedException {
        final Data data = new Data(DATA_NAME, DATA_DESCRIPTION);
        IndexResponse response = openSearchClient.indexData(data, INDEX, DATA_ID);
        Assert.assertEquals(response.index(), INDEX);
        Assert.assertEquals(response.id(), DATA_ID);
        Assert.assertEquals(response.result(), Result.Created);
        //Wait until the server index it! The default refresh interval is one second.
        Thread.sleep(1000);
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
    public void searchDataByQuery() {
        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, new MatchQuery.Builder().field("description")
                .query(FieldValue.of(DATA_DESCRIPTION)).build()._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataByShouldQuery() {
        MatchQuery shouldQuery1 = new MatchQuery.Builder().field("name").query(FieldValue.of(DATA_NAME)).build();
        MatchQuery shouldQuery2 = new MatchQuery.Builder().field("name").query(FieldValue.of("wrong")).build();

        final List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(shouldQuery1._toQuery());
        shouldQueries.add(shouldQuery2._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().should(shouldQueries).minimumShouldMatch("1").build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataByShouldQueryInvalid() {
        MatchQuery shouldQuery1 = new MatchQuery.Builder().field("name").query(FieldValue.of(DATA_NAME)).build();
        MatchQuery shouldQuery2 = new MatchQuery.Builder().field("name").query(FieldValue.of("wrong")).build();
        MatchQuery shouldQuery3 = new MatchQuery.Builder().field("name").query(FieldValue.of("wrong2")).build();

        final List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(shouldQuery1._toQuery());
        shouldQueries.add(shouldQuery2._toQuery());
        shouldQueries.add(shouldQuery3._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().should(shouldQueries).minimumShouldMatch("2").build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 0);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataByMustNotQuery() {
        MatchQuery matchQuery = new MatchQuery.Builder().field("name").query(FieldValue.of(DATA_NAME)).build();

        final List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(matchQuery._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().mustNot(shouldQueries).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 0);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataByMustQuery() {
        MatchQuery matchQuery1 = new MatchQuery.Builder().field("name").query(FieldValue.of(DATA_NAME)).build();
        MatchQuery matchQuery2 = new MatchQuery.Builder().field("name").query(FieldValue.of("wrong")).build();

        final List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(matchQuery1._toQuery());
        mustQueries.add(matchQuery2._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().must(mustQueries).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 0);
    }

    @Test(dependsOnMethods = {"searchData", "getData", "searchDataByQuery", "searchDataByShouldQuery", "searchDataByMustQuery", "searchDataByShouldQueryInvalid", "searchDataByMustNotQuery"})
    public void deleteData() {
        final DeleteResponse response = openSearchClient.deleteData(INDEX, DATA_ID);
        Assert.assertEquals(response.id(), DATA_ID);
        Assert.assertEquals(response.result(), Result.Deleted);
    }

    @AfterClass
    public void cleanUp() {
        openSearchClient.deleteIndex(INDEX);
    }


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
