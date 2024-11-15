package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SortOptionOrder;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch.core.SearchResponse;

import java.util.Collection;
import java.util.List;

public abstract class CategorizedElementRepository<E extends CategorizedElement<?>> extends OpenSearchElementRepository<E> {

    public CategorizedElementRepository(Class<E> elementClass, OpenSearchClient openSearchClient) {
        super(elementClass, openSearchClient);
    }

    public List<E> searchByCategory(String categorization, Integer from, Integer size) {
        return searchByCategories(List.of(categorization), QuantifiersOperator.ANY_OF, from, size);
    }


    public List<E> searchByCategories(Collection<String> categorizations, QuantifiersOperator quantifiersOperator, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        categorizations.forEach(categorization -> {
            shouldHavePredicates.add(Pair.of("categorizations", categorization));
        });
        if (quantifiersOperator == QuantifiersOperator.ALL_OF) {
            shouldHavePredicates.setMinimumShouldMatch(categorizations.size());
        } else {
            shouldHavePredicates.setMinimumShouldMatch(1);
        }
        final SearchResponse<E> response = getOpenSearchClient().searchData(getElementClass(), getOpenSearchIndex(), shouldHavePredicates,
                new SortResultOptions("createdAt", SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);

    }
}
