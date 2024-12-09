package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import com.biit.ks.persistence.opensearch.search.SortOptionOrder;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class OpenSearchElementRepository<E extends OpenSearchElement<?>> {

    private static final String SORTING_FIELD = "createdAt";

    private final Class<E> elementClass;
    private final OpenSearchClient openSearchClient;


    public OpenSearchElementRepository(Class<E> elementClass, OpenSearchClient openSearchClient) {
        this.elementClass = elementClass;
        this.openSearchClient = openSearchClient;
    }

    @PostConstruct
    public void createIndex() {
        try {
            openSearchClient.createIndex(getOpenSearchIndex());
        } catch (OpenSearchException e) {
            if (!e.getMessage().contains("resource_already_exists_exception")) {
                throw e;
            }
        }
    }

    public Class<E> getElementClass() {
        return elementClass;
    }

    public abstract String getOpenSearchIndex();

    public OpenSearchClient getOpenSearchClient() {
        return openSearchClient;
    }


    public E save(E element) {
        try {
            openSearchClient.indexData(element, getOpenSearchIndex(), element.getId() != null ? element.getId().toString() : null);
            return element;
        } finally {
            new Thread(openSearchClient::refreshIndex).start();
        }
    }


    public E update(E element) {
        try {
            openSearchClient.updateData(elementClass, element, getOpenSearchIndex(), element.getId() != null ? element.getId().toString() : null);
            return element;
        } finally {
            new Thread(openSearchClient::refreshIndex).start();
        }
    }


    public Optional<E> get(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        final GetResponse<E> response = openSearchClient.getData(getElementClass(), getOpenSearchIndex(), uuid.toString());
        if (response == null || response.source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.source());
    }

    public List<E> get(String name) {
        if (name == null) {
            return new ArrayList<>();
        }
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        mustHavePredicates.add(Pair.of("name", name));
        final SearchResponse<E> response = getOpenSearchClient().searchData(getElementClass(), getOpenSearchIndex(), mustHavePredicates);
        return getOpenSearchClient().convertResponse(response);
    }


    public List<E> getAll(Integer from, Integer size) {
        final SearchResponse<E> response = getOpenSearchClient().getAll(getElementClass(), getOpenSearchIndex(),
                new SortResultOptions(SORTING_FIELD, SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);
    }

    public void delete(E entity) {
        if (entity != null && entity.getId() != null) {
            try {
                getOpenSearchClient().deleteData(getOpenSearchIndex(), String.valueOf(entity.getId()));
            } finally {
                new Thread(openSearchClient::refreshIndex).start();
            }
        }
    }

    public void delete(UUID uuid) {
        if (uuid != null) {
            try {
                getOpenSearchClient().deleteData(getOpenSearchIndex(), String.valueOf(uuid));
            } finally {
                new Thread(openSearchClient::refreshIndex).start();
            }
        }
    }


    public abstract SearchPredicates searchByValuePredicate(String value, Integer from, Integer size);


    public List<E> search(String value, Integer from, Integer size) {
        final SearchResponse<E> response = getOpenSearchClient().searchData(getElementClass(), getOpenSearchIndex(),
                searchByValuePredicate(value, from, size), new SortResultOptions("createdAt", SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);
    }


    public long count(String value) {
        final CountResponse response = getOpenSearchClient().countData(getElementClass(), getOpenSearchIndex(), searchByValuePredicate(value, null, null));
        return getOpenSearchClient().convertResponse(response);
    }

    public long count() {
        final CountResponse response = getOpenSearchClient().countData(getOpenSearchIndex());
        return getOpenSearchClient().convertResponse(response);
    }


    public List<E> search(SearchPredicates search) {
        final SearchResponse<E> response = getOpenSearchClient().searchData(getElementClass(), getOpenSearchIndex(), search);
        return getOpenSearchClient().convertResponse(response);
    }


    protected ShouldHavePredicates convertSearch(SimpleSearch searchQuery) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        if (searchQuery.getContent() != null && !searchQuery.getContent().isBlank()) {
            shouldHavePredicates.add(Pair.of("name", searchQuery.getContent()));
            shouldHavePredicates.add(Pair.of("description", searchQuery.getContent()));
        }
        if (searchQuery.getOwner() != null && !searchQuery.getOwner().isBlank()) {
            shouldHavePredicates.add(Pair.of("createdBy", searchQuery.getOwner()));
        }
        if (searchQuery.getFrom() != null || searchQuery.getTo() != null) {
            shouldHavePredicates.addRange("createdAt", searchQuery.getFrom(), searchQuery.getTo());
        }
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(searchQuery.getRequiredQueries());
        return shouldHavePredicates;
    }


    public List<E> search(SimpleSearch searchQuery, Integer from, Integer size) {
        if (searchQuery == null) {
            return new ArrayList<>();
        }
        final ShouldHavePredicates shouldHavePredicates = convertSearch(searchQuery);
        if (shouldHavePredicates.getSearch().isEmpty() && shouldHavePredicates.getRanges().isEmpty() && shouldHavePredicates.getCategories().isEmpty()) {
            return new ArrayList<>();
        }
        final SearchResponse<E> response = getOpenSearchClient().searchData(getElementClass(), getOpenSearchIndex(), shouldHavePredicates, from, size);
        return getOpenSearchClient().convertResponse(response);
    }


    public long count(SimpleSearch searchQuery) {
        if (searchQuery == null) {
            return 0;
        }
        final ShouldHavePredicates shouldHavePredicates = convertSearch(searchQuery);
        if (shouldHavePredicates.getSearch().isEmpty() && shouldHavePredicates.getRanges().isEmpty() && shouldHavePredicates.getCategories().isEmpty()) {
            return 0;
        }
        return getOpenSearchClient().convertResponse(getOpenSearchClient().countData(getElementClass(), getOpenSearchIndex(), shouldHavePredicates));
    }
}
