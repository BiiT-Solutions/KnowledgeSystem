package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CategorizedElementProvider<E extends CategorizedElement<?>, R extends CategorizedElementRepository<E>> {

    private final R repository;

    protected CategorizedElementProvider(R repository) {
        this.repository = repository;
    }

    public R getRepository() {
        return repository;
    }

    public List<E> searchByCategories(Set<Categorization> categorizations, QuantifiersOperator quantifiersOperator, Integer from, Integer size) {
        return getRepository().searchByCategories(categorizations.stream().map(Categorization::getName).collect(Collectors.toSet()),
                quantifiersOperator, from, size);
    }

    public List<E> searchByCategories(Collection<String> categorizations, QuantifiersOperator quantifiersOperator, Integer from, Integer size) {
        return getRepository().searchByCategories(categorizations, quantifiersOperator, from, size);
    }
}
