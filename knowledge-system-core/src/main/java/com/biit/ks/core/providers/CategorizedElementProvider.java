package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.ResponseWrapper;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;

import java.util.Collection;

public abstract class CategorizedElementProvider<E extends CategorizedElement<?>, R extends CategorizedElementRepository<E>>
        extends OpenSearchElementProvider<E, R> {

    private final CategorizedElementRepository<E> categorizedElementRepository;

    protected CategorizedElementProvider(R categorizedElementRepository) {
        super(categorizedElementRepository);
        this.categorizedElementRepository = categorizedElementRepository;
    }

    public ResponseWrapper<E> searchByCategory(Categorization categorization, Integer from, Integer size) {
        return categorizedElementRepository.searchByCategory(categorization, from, size);
    }

    public ResponseWrapper<E> searchByCategory(String categorizationName, Integer from, Integer size) {
        return categorizedElementRepository.searchByCategory(categorizationName, from, size);
    }

    public ResponseWrapper<E> searchByCategories(Collection<Categorization> categorizations, QuantifiersOperator quantifiersOperator,
                                                 Integer from, Integer size) {
        return categorizedElementRepository.searchByCategories(categorizations, quantifiersOperator, from, size);
    }

    public ResponseWrapper<E> searchByCategoryNames(Collection<String> categorizations, QuantifiersOperator quantifiersOperator, Integer from, Integer size) {
        return categorizedElementRepository.searchByCategoryNames(categorizations, quantifiersOperator, from, size);
    }

    public long countByCategoryNames(Collection<String> categorizations, QuantifiersOperator quantifiersOperator) {
        return categorizedElementRepository.countByCategoryNames(categorizations, quantifiersOperator);
    }
}
