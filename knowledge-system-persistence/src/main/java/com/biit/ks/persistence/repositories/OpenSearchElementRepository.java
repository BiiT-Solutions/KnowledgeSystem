package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import jakarta.annotation.PostConstruct;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class OpenSearchElementRepository<E extends OpenSearchElement<?>> {

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
        openSearchClient.indexData(element, getOpenSearchIndex(), element.getId() != null ? element.getId().toString() : null);
        return element;
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
        final SearchResponse<E> response = getOpenSearchClient().getAll(getElementClass(), getOpenSearchIndex(), from, size);
        return getOpenSearchClient().convertResponse(response);
    }

    public void delete(E entity) {
        if (entity != null && entity.getId() != null) {
            getOpenSearchClient().deleteData(getOpenSearchIndex(), String.valueOf(entity.getId()));
        }
    }

    public abstract List<E> search(String query, Integer from, Integer size);
}
