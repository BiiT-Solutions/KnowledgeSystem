package com.biit.ks.core.opensearch;

import com.biit.ks.core.opensearch.search.IntervalsSearch;
import com.biit.ks.core.opensearch.search.MustHaveParameters;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsMatch;
import org.opensearch.client.opensearch._types.query_dsl.IntervalsQuery;
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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Test(groups = {"opensearchIntervals"})
public class OpenSearchIntervalsTests extends AbstractTestNGSpringContextTests {

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


    @BeforeClass
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }

    @BeforeClass(dependsOnMethods = "createIndex")
    public void indexData() {
        openSearchClient.indexData(new Data(DATA1_NAME, DATA1_DESCRIPTION, "red"), INDEX, DATA1_ID);
        openSearchClient.indexData(new Data(DATA2_NAME, DATA2_DESCRIPTION, "blue"), INDEX, DATA2_ID);
        openSearchClient.indexData(new Data(DATA3_NAME, DATA3_DESCRIPTION, "red"), INDEX, DATA3_ID);
        openSearchClient.refreshIndex();
    }

    @Test
    public void searchDataWithIntervalsAndGap() {
        //Max gaps 0 → terms must be next to each other.
        final IntervalsSearch mustHaveParameters = new IntervalsSearch();
        mustHaveParameters.addMatch("description", "The Data", 1, true);

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 3);
    }

    @Test
    public void searchDataWithIntervalsAndGapOriginal() {
        IntervalsQuery query = new IntervalsQuery.Builder().field("description").match(new IntervalsMatch.Builder().query("The Data").maxGaps(1).ordered(true)
                .build()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, query._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 3);
    }

    @Test
    public void searchDataWithIntervalsNoGapNotOrdered() {
        //Max gaps 0 → terms must be next to each other.
        final IntervalsSearch mustHaveParameters = new IntervalsSearch();
        mustHaveParameters.addMatch("description", "The Data", 0, false);

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 1);
    }

    @Test
    public void searchDataWithIntervalsAndGapNotOrdered() {
        final IntervalsSearch mustHaveParameters = new IntervalsSearch();
        mustHaveParameters.addMatch("description", "Data The", 1, false);


        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 3);
    }

    @Test
    public void searchDataWithIntervals() {
        //Max gaps 0 → terms must be next to each other.
        final IntervalsSearch mustHaveParameters = new IntervalsSearch();
        mustHaveParameters.addMatch("description", "Data The", 0, false);


        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(priority = 100)
    public void deleteData() {
        final DeleteResponse response = openSearchClient.deleteData(INDEX, DATA1_ID);
        Assert.assertEquals(response.id(), DATA1_ID);
        Assert.assertEquals(response.result(), Result.Deleted);
    }

    @AfterClass(alwaysRun = true)
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
