package com.biit.ks.core.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.opensearch._types.query_dsl.Intervals;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsAllOf;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsMatch;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsPrefix;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsQuery;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsQueryBuilders;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.awt.Color;

@SpringBootTest
@Test(groups = {"opensearchClient"})
public class OpenSearchMixedSearchTests extends AbstractTestNGSpringContextTests {

    private static final String INDEX = "sample-index";
    private static final String DATA1_ID = "1";
    private static final String DATA1_NAME = "firstData";
    private static final String DATA1_DESCRIPTION = "The Data";
    private static final String DATA1_COLOR = Color.BLACK.toString();

    private static final String DATA2_ID = "2";
    private static final String DATA2_NAME = "secondData";
    private static final String DATA2_DESCRIPTION = "The Second Data";

    private static final String DATA2_COLOR = Color.WHITE.toString();

    private static final String DATA3_ID = "3";
    private static final String DATA3_NAME = "thirdData";
    private static final String DATA3_DESCRIPTION = "The Third Data";

    private static final String DATA3_COLOR = Color.BLACK.toString();


    private static final String DATA4_ID = "4";
    private static final String DATA4_NAME = "fourthData";
    private static final String DATA4_DESCRIPTION = "This is another Data";

    private static final String DATA4_COLOR = Color.WHITE.toString();

    private static final String DATA5_ID = "5";
    private static final String DATA5_NAME = "fifthData";
    private static final String DATA5_DESCRIPTION = "The final and last Data";

    private static final String DATA5_COLOR = Color.BLACK.toString();

    @Autowired
    private OpenSearchClient openSearchClient;


    @Test
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }

    @Test(dependsOnMethods = "createIndex")
    public void indexData() throws InterruptedException {
        openSearchClient.indexData(new Data(DATA1_NAME, DATA1_DESCRIPTION, DATA1_COLOR), INDEX, DATA1_ID);
        openSearchClient.indexData(new Data(DATA2_NAME, DATA2_DESCRIPTION, DATA2_COLOR), INDEX, DATA2_ID);
        openSearchClient.indexData(new Data(DATA3_NAME, DATA3_DESCRIPTION, DATA3_COLOR), INDEX, DATA3_ID);
        openSearchClient.indexData(new Data(DATA4_NAME, DATA4_DESCRIPTION, DATA4_COLOR), INDEX, DATA4_ID);
        openSearchClient.indexData(new Data(DATA5_NAME, DATA5_DESCRIPTION, DATA5_COLOR), INDEX, DATA5_ID);
        //Wait until the server index it! The default refresh interval is one second.
        Thread.sleep(1000);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithIntervalsAndGap() {
        //Max gaps 0 --> terms must be next to each other.
        Intervals intervals = new Intervals.Builder().match(new IntervalsMatch.Builder().useField("description").query("The Data").maxGaps(3).ordered(true)
                .build()).build();

        IntervalsAllOf allQueries = IntervalsQueryBuilders.allOf().intervals(intervals).build();
        IntervalsQuery query = new IntervalsQuery.Builder().field("description").allOf(allQueries).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, query._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 4);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithPrefixOnWords() {
        IntervalsQuery query = new IntervalsQuery.Builder().field("description").prefix(new IntervalsPrefix.Builder().useField("description").prefix("Th").build()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, query._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 4);
    }


//    @Test(dependsOnMethods = "indexData")
//    public void searchDataMixedIntervals() {
//        MatchQuery shouldMatchQuery1 = new MatchQuery.Builder().field("gender").query(FieldValue.of("male")).build();
//        MatchQuery shouldMatchQuery2 = new MatchQuery.Builder().field("gender").query(FieldValue.of("female")).build();
//
//        List<Query> shouldQueries = new ArrayList<>();
//        shouldQueries.add(shouldMatchQuery1._toQuery());
//        shouldQueries.add(shouldMatchQuery2._toQuery());
//
//        final List<Intervals> intervalsQueries = new ArrayList<>();
//        //Max gaps 0 --> terms must be next to each other.
//        intervalsQueries.add(new Intervals.Builder().match(new IntervalsMatch.Builder().useField("description").query("The Data").maxGaps(0).ordered(true)
//                .build()).build());
//        //intervalsQueries.add(new Intervals.Builder().match(new IntervalsFuzzy.Builder().useField("gendr").fuzziness("AUTO").build()).build());
//
//        //IntervalsAllOf intervalsAllOfQuery = IntervalsQueryBuilders.allOf().ordered(true).intervals(intervalsQueries).build();
//
////        BoolQuery boolQuery = new BoolQuery.Builder().should(shouldQueries).minimumShouldMatch("1").build();
////        IntervalsMatch intervalsMatch = new IntervalsMatch.Builder().query(boolQuery._toQuery());
////
////        //IntervalsMatch intervalsMatch = new IntervalsMatch.Builder().query(shouldMatchQuery1._toQuery().match()._toQuery()).build();
////
////
////        final List<Intervals> intervalsQueries = new ArrayList<>();
////        intervalsQueries.add(new Intervals.Builder().match());
////        intervalsQueries.add(shouldMatchQuery1._toQuery());
////
////        IntervalsAnyOf intervalsAnyOf = new IntervalsAnyOf.Builder().intervals(intervalsQueries).build();
////        //Intervals intervals = new Intervals.Builder().anyOf(intervalsAnyOf).build();
////
////
////        //Intervals intervalsQuery = new Intervals.Builder().allOf(new IntervalsAnyOf.Builder().intervals(intervalsQueries).build()).build();
////        IntervalsQuery intervalsQuery = new IntervalsQuery.Builder().allOf(intervals).build();
//
//        final SearchResponse<OpenSearchClientTests.Data> response = openSearchClient.searchData(Data.class, intervalsAllOfQuery._toQuery());
//        Assert.assertEquals(response.hits().hits().size(), 1);
//    }


    @AfterClass
    public void cleanUp() {
        openSearchClient.deleteIndex(INDEX);
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Data {
        private String name;
        private String description;

        private String color;

        public Data() {
        }

        public Data(String name, String description, String color) {
            this.name = name;
            this.description = description;
            this.color = color;
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

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

}
