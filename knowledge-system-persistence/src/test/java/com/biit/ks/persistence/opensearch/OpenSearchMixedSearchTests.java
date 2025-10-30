package com.biit.ks.persistence.opensearch;

/*-
 * #%L
 * Knowledge System (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.ks.persistence.opensearch.search.IntervalsSearch;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.PutIndicesSettingsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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


    @BeforeClass
    public void createIndex() {
        final PutIndicesSettingsResponse response = openSearchClient.createIndex(INDEX);
        Assert.assertTrue(response.acknowledged());
    }

    @BeforeClass(dependsOnMethods = "createIndex")
    public void indexData() {
        openSearchClient.indexData(new Data(DATA1_NAME, DATA1_DESCRIPTION, DATA1_COLOR), INDEX, DATA1_ID);
        openSearchClient.indexData(new Data(DATA2_NAME, DATA2_DESCRIPTION, DATA2_COLOR), INDEX, DATA2_ID);
        openSearchClient.indexData(new Data(DATA3_NAME, DATA3_DESCRIPTION, DATA3_COLOR), INDEX, DATA3_ID);
        openSearchClient.indexData(new Data(DATA4_NAME, DATA4_DESCRIPTION, DATA4_COLOR), INDEX, DATA4_ID);
        openSearchClient.indexData(new Data(DATA5_NAME, DATA5_DESCRIPTION, DATA5_COLOR), INDEX, DATA5_ID);
        openSearchClient.refreshIndex();
    }


    @Test
    public void searchDataWithIntervalsAndWildCard() {
        final IntervalsSearch intervalsSearch = new IntervalsSearch();
        //DATA4 not matching.
        intervalsSearch.addMatch("description", "The Data", 3, true);
        //DATA5 not matching.
        intervalsSearch.addWildcard("name", "*Data");

        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, INDEX, intervalsSearch);
        Assert.assertEquals(response.hits().hits().size(), 3);
    }


    @Test
    public void searchDataWithIntervalsAndWildCardAny() {
        final IntervalsSearch intervalsSearch = new IntervalsSearch();
        //DATA4 not matching.
        intervalsSearch.addMatch("description", "The Data", 3, true);
        //DATA5 not matching.
        intervalsSearch.addWildcard("name", "*Data");
        intervalsSearch.setIntervalsSearchOperator(QuantifiersOperator.ANY_OF);


        final SearchResponse<Data> response = openSearchClient.searchData(Data.class, INDEX, intervalsSearch);
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
