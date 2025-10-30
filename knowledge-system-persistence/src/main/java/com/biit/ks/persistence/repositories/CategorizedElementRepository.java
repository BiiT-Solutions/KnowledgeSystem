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

import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import com.biit.ks.persistence.opensearch.search.SortOptionOrder;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.SearchResponse;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CategorizedElementRepository<E extends CategorizedElement<?>> extends OpenSearchElementRepository<E> {

    public CategorizedElementRepository(Class<E> elementClass, OpenSearchClient openSearchClient) {
        super(elementClass, openSearchClient);
    }

    public SearchWrapper<E> searchByCategory(Categorization categorization, Integer from, Integer size) {
        return searchByCategories(List.of(categorization), QuantifiersOperator.ANY_OF, from, size);
    }

    public SearchWrapper<E> searchByCategory(String categorizationName, Integer from, Integer size) {
        return searchByCategoryNames(List.of(categorizationName), QuantifiersOperator.ANY_OF, from, size);
    }


    public SearchWrapper<E> searchByCategories(Collection<Categorization> categorizations, QuantifiersOperator quantifiersOperator,
                                               Integer from, Integer size) {
        return searchByCategoryNames(categorizations.stream().map(OpenSearchElement::getName).collect(Collectors.toSet()), quantifiersOperator, from, size);
    }

    public SearchWrapper<E> searchByCategoryNames(Collection<String> categorizationsNames, QuantifiersOperator quantifiersOperator,
                                                  Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        categorizationsNames.forEach(categorization ->
                shouldHavePredicates.addCategory(Pair.of("categorizations.name", categorization)));
        if (quantifiersOperator == QuantifiersOperator.ALL_OF) {
            shouldHavePredicates.setMinimumShouldMatch(categorizationsNames.size());
        } else {
            shouldHavePredicates.setMinimumShouldMatch(1);
        }
        final SearchResponse<E> response = getOpenSearchClient().searchData(getElementClass(), getOpenSearchIndex(), shouldHavePredicates,
                new SortResultOptions("createdAt", SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);
    }


    public long countByCategoryNames(Collection<String> categorizationsNames, QuantifiersOperator quantifiersOperator) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        categorizationsNames.forEach(categorization ->
                shouldHavePredicates.addCategory(Pair.of("categorizations.name", categorization)));
        if (quantifiersOperator == QuantifiersOperator.ALL_OF) {
            shouldHavePredicates.setMinimumShouldMatch(categorizationsNames.size());
        } else {
            shouldHavePredicates.setMinimumShouldMatch(1);
        }
        final CountResponse response = getOpenSearchClient().countData(getElementClass(), getOpenSearchIndex(), shouldHavePredicates);
        return getOpenSearchClient().convertResponse(response);
    }


    @Override
    protected ShouldHavePredicates convertSearch(SimpleSearch searchQuery) {
        final ShouldHavePredicates shouldHavePredicates = super.convertSearch(searchQuery);
        if (searchQuery.getKeywords() != null && !searchQuery.getKeywords().isEmpty()) {
            searchQuery.getKeywords().forEach(keywords ->
                    shouldHavePredicates.addCategory(Pair.of("categorizations.name", keywords)));
        }
        return shouldHavePredicates;
    }
}
