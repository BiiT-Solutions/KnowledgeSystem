package com.biit.ks.core.seaweed.opensearch;

import com.biit.ks.core.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Intervals;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsMatch;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.DeleteResponse;
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
public class OpenSearchComplexSearchTests extends AbstractTestNGSpringContextTests {

    private static final String INDEX = "sample-index";
    private static final String DATA1_ID = "1";
    private static final String DATA1_NAME = "firstData";
    private static final String DATA1_DESCRIPTION = "The Data";

    private static final String DATA2_ID = "2";
    private static final String DATA2_NAME = "secondData";
    private static final String DATA2_DESCRIPTION = "The Second Data";

    private static final String DATA3_ID = "3";
    private static final String DATA3_NAME = "thirdData";
    private static final String DATA3_DESCRIPTION = "The Third Data";

    @Autowired
    private OpenSearchClient openSearchClient;


    @Test
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }

    @Test(dependsOnMethods = "createIndex")
    public void indexData() throws InterruptedException {
        openSearchClient.indexData(new Data(DATA1_NAME, DATA1_DESCRIPTION, "male"), INDEX, DATA1_ID);
        openSearchClient.indexData(new Data(DATA2_NAME, DATA2_DESCRIPTION, "female"), INDEX, DATA2_ID);
        openSearchClient.indexData(new Data(DATA3_NAME, DATA3_DESCRIPTION, "male"), INDEX, DATA3_ID);
        //Wait until the server index it! The default refresh interval is one second.
        Thread.sleep(1000);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithShould() {
        MatchQuery shouldMatchQuery1 = new MatchQuery.Builder().field("gender").query(FieldValue.of("male")).build();
        MatchQuery shouldMatchQuery2 = new MatchQuery.Builder().field("gender").query(FieldValue.of("female")).build();

        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(shouldMatchQuery1._toQuery());
        shouldQueries.add(shouldMatchQuery2._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().should(shouldQueries).minimumShouldMatch("1").build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 3);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithMustAndShould() {
        MatchQuery matchQuery = new MatchQuery.Builder().field("name").query(FieldValue.of(DATA1_NAME)).build();

        // create a list of queries
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(matchQuery._toQuery());

        MatchQuery shouldMatchQuery1 = new MatchQuery.Builder().field("gender").query(FieldValue.of("male")).build();
        MatchQuery shouldMatchQuery2 = new MatchQuery.Builder().field("gender").query(FieldValue.of("female")).build();

        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(shouldMatchQuery1._toQuery());
        shouldQueries.add(shouldMatchQuery2._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().must(mustQueries).should(shouldQueries).minimumShouldMatch("1").build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithIntervals() {
        //Max gaps 0 --> terms must be next to each other.
        Intervals intervals = new Intervals.Builder().match(new IntervalsMatch.Builder().useField("description").query("The Data").maxGaps(0).ordered(true)
                .build()).build();

        Query.of(intervals.match());

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, intervals.match());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataMixedIntervals() {
        MatchQuery shouldMatchQuery1 = new MatchQuery.Builder().field("gender").query(FieldValue.of("male")).build();
        MatchQuery shouldMatchQuery2 = new MatchQuery.Builder().field("gender").query(FieldValue.of("female")).build();

        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(shouldMatchQuery1._toQuery());
        shouldQueries.add(shouldMatchQuery2._toQuery());

        final List<Intervals> intervalsQueries = new ArrayList<>();
        //Max gaps 0 --> terms must be next to each other.
        intervalsQueries.add(new Intervals.Builder().match(new IntervalsMatch.Builder().useField("description").query("The Data").maxGaps(0).ordered(true)
                .build()).build());
        //intervalsQueries.add(new Intervals.Builder().match(new IntervalsFuzzy.Builder().useField("gendr").fuzziness("AUTO").build()).build());

        //IntervalsAllOf intervalsAllOfQuery = IntervalsQueryBuilders.allOf().ordered(true).intervals(intervalsQueries).build();

//        BoolQuery boolQuery = new BoolQuery.Builder().should(shouldQueries).minimumShouldMatch("1").build();
//        IntervalsMatch intervalsMatch = new IntervalsMatch.Builder().query(boolQuery._toQuery());
//
//        //IntervalsMatch intervalsMatch = new IntervalsMatch.Builder().query(shouldMatchQuery1._toQuery().match()._toQuery()).build();
//
//
//        final List<Intervals> intervalsQueries = new ArrayList<>();
//        intervalsQueries.add(new Intervals.Builder().match());
//        intervalsQueries.add(shouldMatchQuery1._toQuery());
//
//        IntervalsAnyOf intervalsAnyOf = new IntervalsAnyOf.Builder().intervals(intervalsQueries).build();
//        //Intervals intervals = new Intervals.Builder().anyOf(intervalsAnyOf).build();
//
//
//        //Intervals intervalsQuery = new Intervals.Builder().allOf(new IntervalsAnyOf.Builder().intervals(intervalsQueries).build()).build();
//        IntervalsQuery intervalsQuery = new IntervalsQuery.Builder().allOf(intervals).build();

        final SearchResponse<OpenSearchClientTests.Data> response = openSearchClient.searchData(Data.class, intervalsAllOfQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = {})
    public void deleteData() {
        final DeleteResponse response = openSearchClient.deleteData(INDEX, DATA1_ID);
        Assert.assertEquals(response.id(), DATA1_ID);
        Assert.assertEquals(response.result(), Result.Deleted);
    }

    @AfterClass
    public void cleanUp() {
        openSearchClient.deleteIndex(INDEX);
    }


    static class Data {
        private String name;
        private String description;

        private String gender;

        public Data() {
        }

        public Data(String name, String description, String gender) {
            this.name = name;
            this.description = description;
            this.gender = gender;
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

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }

}
