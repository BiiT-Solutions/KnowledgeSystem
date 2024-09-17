package com.biit.ks.core.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsAnyOf;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsMatch;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsPrefix;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsQuery;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsQueryBuilders;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsWildcard;
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
@Test(groups = {"opensearchClient"})
public class OpenSearchMixedSearchTests extends AbstractTestNGSpringContextTests {

    private static final String INDEX = "sample-index";
    private static final String DATA1_ID = "1";
    private static final String DATA1_NAME = "firstData";
    private static final String DATA1_DESCRIPTION = "The Data";
    private static final String DATA1_COLOR = "black";

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
    private static final String DATA5_DESCRIPTION = "The final and last Data";

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
    public void searchDataWithPrefixOnWords() {
        IntervalsQuery query = new IntervalsQuery.Builder().field("description").prefix(new IntervalsPrefix.Builder().useField("description").prefix("Th").build()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, query._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 5);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataWithIntervalsAndWildCard() {
        //DATA4 not matching.
        IntervalsMatch intervalsDescription = new IntervalsMatch.Builder().useField("description").query("The Data").maxGaps(3).ordered(true)
                .build();
        //DATA5 not matching.
        IntervalsWildcard intervalsName = new IntervalsWildcard.Builder().useField("name").pattern("*Data").build();

        IntervalsQuery intervalsDescriptionQuery = new IntervalsQuery.Builder().field("description").match(intervalsDescription).build();
        IntervalsQuery intervalsNameQuery = new IntervalsQuery.Builder().field("name").wildcard(intervalsName).build();

        List<Query> matchQueries = new ArrayList<>();
        matchQueries.add(intervalsDescriptionQuery._toQuery());
        matchQueries.add(intervalsNameQuery._toQuery());

        BoolQuery boolQuery = new BoolQuery.Builder().must(matchQueries).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 3);
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithIntervalsAndWildCardAny() {
        //DATA4 not matching.
        IntervalsMatch intervalsDescription = new IntervalsMatch.Builder().useField("description").query("The Data").maxGaps(3).ordered(true)
                .build();
        //DATA5 not matching.
        IntervalsWildcard intervalsName = new IntervalsWildcard.Builder().useField("name").pattern("*Data").build();


        IntervalsAnyOf intervalsAnyOf = IntervalsQueryBuilders.anyOf()
                .intervals(intervalsDescription._toIntervals(), intervalsName._toIntervals()).build();


        //Why I need a field here, and can be name or description?
        IntervalsQuery query = new IntervalsQuery.Builder().field("name").anyOf(intervalsAnyOf).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, query._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 5);
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
