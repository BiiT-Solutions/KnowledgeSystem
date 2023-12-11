package com.biit.ks.core.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
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
@Test(groups = {"opensearchFuzzy"})
public class OpenSearchFuzzySearchTests extends AbstractTestNGSpringContextTests {

    private static final String INDEX = "sample-index";
    private static final String DATA1_ID = "1";
    private static final String DATA1_NAME = "firstData";
    private static final String DATA1_DESCRIPTION = "The Data";
    private static final String DATA1_COLOR = "blue";

    private static final String DATA2_ID = "2";
    private static final String DATA2_NAME = "secondData";
    private static final String DATA2_DESCRIPTION = "The Second Data";

    private static final String DATA2_COLOR = "white";

    private static final String DATA3_ID = "3";
    private static final String DATA3_NAME = "thirdData";
    private static final String DATA3_DESCRIPTION = "The Third Data";

    private static final String DATA3_COLOR = "black";


    private static final String DATA4_ID = "4";
    private static final String DATA4_NAME = "fourthData";
    private static final String DATA4_DESCRIPTION = "This is another Data";

    private static final String DATA4_COLOR = "white";

    private static final String DATA5_ID = "5";
    private static final String DATA5_NAME = "fifthDatum";
    private static final String DATA5_DESCRIPTION = "The final world words works";

    private static final String DATA5_COLOR = "black";

    @Autowired
    private OpenSearchClient openSearchClient;


    @Test
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }

    @Test(dependsOnMethods = "createIndex")
    public void indexData() {
        openSearchClient.indexData(new Data(DATA1_NAME, DATA1_DESCRIPTION, DATA1_COLOR), INDEX, DATA1_ID);
        openSearchClient.indexData(new Data(DATA2_NAME, DATA2_DESCRIPTION, DATA2_COLOR), INDEX, DATA2_ID);
        openSearchClient.indexData(new Data(DATA3_NAME, DATA3_DESCRIPTION, DATA3_COLOR), INDEX, DATA3_ID);
        openSearchClient.indexData(new Data(DATA4_NAME, DATA4_DESCRIPTION, DATA4_COLOR), INDEX, DATA4_ID);
        openSearchClient.indexData(new Data(DATA5_NAME, DATA5_DESCRIPTION, DATA5_COLOR), INDEX, DATA5_ID);
        openSearchClient.refreshIndex();
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithFuzzy() {
        //DATA4 not matching.
        MatchQuery fuzzyQuery = new MatchQuery.Builder().field("description").query(FieldValue.of("The Paca")).fuzziness(Fuzziness.AUTO.tag()).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(fuzzyQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 4);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithFuzzy2() {
        //DATA4 only matching.
        MatchQuery fuzzyQuery = new MatchQuery.Builder().field("description").query(FieldValue.of("This Paca")).fuzziness(Fuzziness.AUTO.tag()).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(fuzzyQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithFuzzy3() {
        //DATA1 only matching.
        MatchQuery fuzzyQuery = new MatchQuery.Builder().field("color").query(FieldValue.of("bluey")).fuzziness(Fuzziness.AUTO.tag()).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(fuzzyQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithMultipleFuzziness() {
        //DATA1 only matching.
        MatchQuery matchQuery1 = new MatchQuery.Builder().field("color").query(FieldValue.of("bluey")).fuzziness(Fuzziness.AUTO.tag())
                .maxExpansions(1).build();

        final List<Query> matchQueries = new ArrayList<>();
        matchQueries.add(matchQuery1._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().must(matchQueries).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithMaxFuzzinessExpansions() {
        //DATA5 only matching.
        MatchQuery matchQuery = new MatchQuery.Builder().field("description").query(FieldValue.of("worl")).fuzziness(Fuzziness.AUTO.tag())
                .maxExpansions(1).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(matchQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test(dependsOnMethods = "indexData", enabled = false)
    public void searchDataWithMaxFuzzinessPrefix() {
        //NO idea how prefix is working.
        MatchQuery matchQuery = new MatchQuery.Builder().field("description").query(FieldValue.of("WOrl")).fuzziness(Fuzziness.AUTO.tag())
                .prefixLength(100).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(matchQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 0);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithMustAndShouldFuzzy() {
        MatchQuery matchQuery = new MatchQuery.Builder().field("name").query(FieldValue.of(DATA1_NAME)).build();

        // create a list of queries
        List<Query> mustQueries = new ArrayList<>();
        mustQueries.add(matchQuery._toQuery());

        MatchQuery shouldMatchQuery1 = new MatchQuery.Builder().field("color").query(FieldValue.of("red")).build();
        MatchQuery shouldMatchQuery2 = new MatchQuery.Builder().field("color").query(FieldValue.of("bluey")).fuzziness(Fuzziness.AUTO.tag()).build();

        List<Query> shouldQueries = new ArrayList<>();
        shouldQueries.add(shouldMatchQuery1._toQuery());
        shouldQueries.add(shouldMatchQuery2._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().must(mustQueries).should(shouldQueries).minimumShouldMatch("1").build();

        final SearchResponse<OpenSearchIntervalsTests.Data> response = openSearchClient.searchData(OpenSearchIntervalsTests.Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


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
