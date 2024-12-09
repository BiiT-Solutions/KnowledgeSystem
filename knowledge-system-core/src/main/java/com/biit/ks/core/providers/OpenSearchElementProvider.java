package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OpenSearchElementProvider<E extends OpenSearchElement<?>, R extends OpenSearchElementRepository<E>> {

    private final R openSearchElementRepository;


    public OpenSearchElementProvider(R openSearchElementRepository) {
        this.openSearchElementRepository = openSearchElementRepository;
    }


    public List<E> search(SearchPredicates searchPredicates) {
        return getRepository().search(searchPredicates);
    }


    public List<E> search(SimpleSearch searchQuery, Integer from, Integer size) {
        return getRepository().search(searchQuery, from, size);
    }


    public R getRepository() {
        return openSearchElementRepository;
    }

    public E save(E element) {
        return getRepository().save(element);
    }


    public E update(E element) {
        return getRepository().update(element);
    }


    public void delete(E element) {
        getRepository().delete(element);
    }


    public void delete(UUID id) {
        getRepository().delete(id);
    }


    public List<E> search(String value, Integer from, Integer size) {
        return getRepository().search(value, from, size);
    }

    public long count() {
        return getRepository().count();
    }

    public long count(String value) {
        return getRepository().count(value);
    }

    public long count(SimpleSearch simpleSearch) {
        return getRepository().count(simpleSearch);
    }


    public Optional<E> get(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        return getRepository().get(uuid);
    }

    public List<E> getAll(Integer from, Integer size) {
        return getRepository().getAll(from, size);
    }
}
