package com.biit.ks.persistence.repositories;

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

import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SortOptionOrder;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

@Repository
public class TextRepository extends CategorizedElementRepository<Text> {

    private final IOpenSearchConfigurator openSearchConfigurator;

    public TextRepository(OpenSearchClient openSearchClient, IOpenSearchConfigurator openSearchConfigurator) {
        super(Text.class, openSearchClient);
        this.openSearchConfigurator = openSearchConfigurator;
    }

    @Override
    public String getOpenSearchIndex() {
        return openSearchConfigurator.getOpenSearchTextIndex();
    }


    public SearchWrapper<Text> search(String query, TextLanguages language, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("description", query));
        shouldHavePredicates.add(Pair.of("name", query));
        shouldHavePredicates.add(Pair.of("content." + language.name(), query));
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        final SearchResponse<Text> response = getOpenSearchClient().searchData(Text.class, getOpenSearchIndex(),
                shouldHavePredicates, new SortResultOptions("createdAt", SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);
    }

    @Override

    public SearchPredicates searchByValuePredicate(String value, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("description", value));
        shouldHavePredicates.add(Pair.of("name", value));
        //Add any language here.
        for (TextLanguages language : TextLanguages.values()) {
            shouldHavePredicates.add(Pair.of("content." + language.name(), value));
        }
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        return shouldHavePredicates;
    }
}
