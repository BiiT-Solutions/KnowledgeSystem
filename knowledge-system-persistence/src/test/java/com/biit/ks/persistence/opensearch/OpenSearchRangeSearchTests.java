package com.biit.ks.persistence.opensearch;

import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

@SpringBootTest
@Test(groups = {"opensearchRange"})
public class OpenSearchRangeSearchTests extends AbstractTestNGSpringContextTests {


    private static final String INDEX = "sample-index";
    private static final String DATA1_ID = "1";
    private static final String DATA1_NAME = "firstData";
    private static final String DATA1_DESCRIPTION = "The Data";
    private static final Integer DATA1_VALUE = 1;

    private static final String DATA2_ID = "2";
    private static final String DATA2_NAME = "secondData";
    private static final String DATA2_DESCRIPTION = "The Second Data";

    private static final Integer DATA2_VALUE = 2;

    private static final String DATA3_ID = "3";
    private static final String DATA3_NAME = "thirdData";
    private static final String DATA3_DESCRIPTION = "The Third Data";

    private static final Integer DATA3_VALUE = 3;


    private static final String DATA4_ID = "4";
    private static final String DATA4_NAME = "fourthData";
    private static final String DATA4_DESCRIPTION = "This is another Data";

    private static final Integer DATA4_VALUE = 4;

    private static final String DATA5_ID = "5";
    private static final String DATA5_NAME = "fifthDatum";
    private static final String DATA5_DESCRIPTION = "The final world words works";

    private static final Integer DATA5_VALUE = 5;

    @Autowired
    private OpenSearchClient openSearchClient;


    @BeforeClass
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }

    @BeforeClass(dependsOnMethods = "createIndex")
    public void indexData() {
        openSearchClient.indexData(new Data(DATA1_NAME, DATA1_DESCRIPTION, DATA1_VALUE, LocalDateTime.now().minusDays(1)), INDEX, DATA1_ID);
        openSearchClient.indexData(new Data(DATA2_NAME, DATA2_DESCRIPTION, DATA2_VALUE, LocalDateTime.now()), INDEX, DATA2_ID);
        openSearchClient.indexData(new Data(DATA3_NAME, DATA3_DESCRIPTION, DATA3_VALUE, LocalDateTime.now().plusDays(1)), INDEX, DATA3_ID);
        openSearchClient.indexData(new Data(DATA4_NAME, DATA4_DESCRIPTION, DATA4_VALUE, LocalDateTime.now().minusYears(1)), INDEX, DATA4_ID);
        openSearchClient.indexData(new Data(DATA5_NAME, DATA5_DESCRIPTION, DATA5_VALUE, LocalDateTime.now().plusYears(1)), INDEX, DATA5_ID);
        openSearchClient.refreshIndex();
    }

    @Test
    public void searchDataByRange() {
        //DATE3 and DATA4
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.addRange("value", 2, 5);

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 2);
    }

    @Test
    public void searchDataDateByRange() {
        //DATE1, DATA2 and DATA4
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.addRange("dateTime", null, LocalDateTime.now());

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 3);
    }

    @Test
    public void searchDataDateByRangeOriginal() {
        //DATE1, DATA2 and DATA4
        RangeQuery rangeQuery = new RangeQuery.Builder().field("dateTime").lt(JsonData.of(
                LocalDateTime.now())).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(rangeQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 3);
    }

    @Test
    public void searchDataValueByRange() {
        //DATE1, DATA2 and DATA3
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.addRange("value", null, 4);
        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustHaveParameters);
        Assert.assertEquals(response.hits().hits().size(), 3);
    }


    @Test
    public void searchDataValueByRangeOriginal() {
        //DATE1, DATA2 and DATA3
        RangeQuery rangeQuery = new RangeQuery.Builder().field("value").lt(JsonData.of(4)).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(rangeQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 3);
    }


    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        openSearchClient.deleteIndex(INDEX);
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Data {
        private String name;
        private String description;

        private Integer value;

        private LocalDateTime dateTime;

        public Data() {
        }

        public Data(String name, String description, Integer value, LocalDateTime dateTime) {
            this.name = name;
            this.description = description;
            this.value = value;
            this.dateTime = dateTime;
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

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
    }

}
