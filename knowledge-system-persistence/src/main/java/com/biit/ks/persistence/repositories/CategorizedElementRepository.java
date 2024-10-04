package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.data.util.Pair;

import java.util.Collection;
import java.util.List;

public class CategorizedElementRepository<E extends CategorizedElement<?>> {


    private final Class<E> elementClass;
    private final OpenSearchClient openSearchClient;


    public CategorizedElementRepository(Class<E> elementClass, OpenSearchClient openSearchClient) {
        this.elementClass = elementClass;
        this.openSearchClient = openSearchClient;
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
        final SearchResponse<E> response = openSearchClient.searchData(elementClass, shouldHavePredicates, from, size);
        return openSearchClient.convertResponse(response);

    }
}
