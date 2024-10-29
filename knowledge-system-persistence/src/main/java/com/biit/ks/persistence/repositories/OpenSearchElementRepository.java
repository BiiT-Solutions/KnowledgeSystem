package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.SortOptionOrder;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import jakarta.annotation.PostConstruct;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchResponse;

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
        final GetResponse<E> response = openSearchClient.getData(elementClass, getOpenSearchIndex(), uuid.toString());
        if (response == null || response.source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.source());
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

    public abstract List<E> search(String query, Integer from, Integer size);
}
