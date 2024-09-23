package com.biit.ks.persistence.opensearch;

import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

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

    private static final String DATA6_ID = "6";
    private static final String DATA6_NAME = "sixthDatum";
    private static final String DATA6_DESCRIPTION = "World Wrestling Federation";

    private static final String DATA6_COLOR = "brown";

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
        openSearchClient.indexData(new Data(DATA6_NAME, DATA6_DESCRIPTION, DATA6_COLOR), INDEX, DATA6_ID);
        openSearchClient.refreshIndex();
    }

    @Test(dependsOnMethods = "indexData")
    public void searchDataWithFuzzyOriginal() {
        //DATA4 not matching.
        MatchQuery fuzzyQuery = new MatchQuery.Builder().field("description").query(FieldValue.of("The Paca")).fuzziness(Fuzziness.AUTO.tag()).build();

        BoolQuery boolQuery = new BoolQuery.Builder().must(fuzzyQuery._toQuery()).build();

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, boolQuery._toQuery());
        Assert.assertEquals(response.hits().hits().size(), 4);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataWithFuzzy() {
        final MustHavePredicates mustParameters = new MustHavePredicates();
        mustParameters.add(Pair.of("description", "This Paca"));
        mustParameters.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustParameters);
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataWithFuzzy2() {
        //DATA1 only matching.
        final MustHavePredicates mustParameters = new MustHavePredicates();
        mustParameters.add(Pair.of("color", "bluey"));
        mustParameters.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustParameters);
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataWithMultipleFuzziness() {
        //DATA1 only matching.
        final MustHavePredicates mustParameters = new MustHavePredicates();
        mustParameters.add(Pair.of("color", "bluey"));
        mustParameters.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO, 1));

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustParameters);
        Assert.assertEquals(response.hits().hits().size(), 1);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataWithMaxFuzzinessExpansions() {
        //DATA5 only matching.
        final MustHavePredicates mustParameters = new MustHavePredicates();
        mustParameters.add(Pair.of("description", "worl"));
        mustParameters.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO, 1));

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, mustParameters);
        Assert.assertEquals(response.hits().hits().size(), 2);
    }


    @Test(dependsOnMethods = "indexData", enabled = false)
    public void searchDataWithFuzzinessPrefixNotMatching() {
        //Prefix must be an exact match before fuzziness.
        final MustHavePredicates mustParameters = new MustHavePredicates();
        mustParameters.add(Pair.of("description", "Worl"));
        mustParameters.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO, null, 1));

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class,
                mustParameters);
        Assert.assertEquals(response.hits().hits().size(), 0);
    }


    @Test(dependsOnMethods = "indexData", enabled = false)
    public void searchDataWithFuzzinessPrefixMatching() {
        //Prefix here, forces the "W" in capitals to match. DATA6 matches but DATA5 not.
        final MustHavePredicates mustParameters = new MustHavePredicates();
        mustParameters.add(Pair.of("description", "Worl"));
        mustParameters.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class,
                mustParameters);
        Assert.assertEquals(response.hits().hits().size(), 0);
    }


    @Test(dependsOnMethods = "indexData")
    public void searchDataWithMustAndShouldFuzzy() {
        final MustHavePredicates mustParameters = new MustHavePredicates();
        mustParameters.add(Pair.of("name", DATA1_NAME));

        final ShouldHavePredicates shouldParameters = new ShouldHavePredicates();
        shouldParameters.add(Pair.of("color", "red"));
        shouldParameters.add(Pair.of("color", "bluey"));
        shouldParameters.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));

        final SearchResponse<Data> response = openSearchClient.searchData(new SearchQuery<>(Data.class,
                mustParameters, shouldParameters));
        Assert.assertEquals(response.hits().hits().size(), 1);
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
