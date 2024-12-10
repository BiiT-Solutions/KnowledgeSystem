package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.OpenSearchElementConverter;
import com.biit.ks.core.converters.models.OpenSearchElementConverterRequest;
import com.biit.ks.core.providers.OpenSearchElementProvider;
import com.biit.ks.dto.OpenSearchElementDTO;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;
import com.biit.server.controller.SimpleController;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class OpenSearchElementController<
        E extends OpenSearchElement<?>,
        D extends OpenSearchElementDTO<?>,
        R extends OpenSearchElementRepository<E>,
        P extends OpenSearchElementProvider<E, R>,
        Rq extends OpenSearchElementConverterRequest<E>,
        Cv extends OpenSearchElementConverter<E, D, Rq>>
        extends SimpleController<E, D, P, Rq, Cv> {

    protected OpenSearchElementController(P provider, Cv converter) {
        super(provider, converter);
    }


    protected SearchWrapper<D> convert(SearchWrapper<E> entities) {
        return convertAll(entities);
    }


    protected SearchWrapper<D> convertAll(SearchWrapper<E> entities) {
        final SearchWrapper<D> response = new SearchWrapper<>(this.getConverter().convertAll(
                entities.getData().stream().map(this::createConverterRequest).collect(Collectors.toList())));
        response.setTotalElements(entities.getTotalElements());
        return response;
    }


    public SearchWrapper<D> search(SimpleSearch searchQuery, Integer from, Integer size) {
        final SearchWrapper<E> results = getProvider().search(searchQuery, from, size);
        return convertAll(results);
    }


    public SearchWrapper<D> search(String value, Integer from, Integer size) {
        final SearchWrapper<E> results = getProvider().search(value, from, size);
        return convertAll(results);
    }

    public long count(SimpleSearch searchQuery) {
        return getProvider().count(searchQuery);
    }


    public long count(String value) {
        return getProvider().count(value);
    }


    public SearchWrapper<D> get(UUID uuid) {
        final SearchWrapper<E> fileEntry = getProvider().get(uuid);
        return convertAll(fileEntry);
    }


    public D update(D data, String updatedBy) {
        data.setUpdatedBy(updatedBy);
        data.setUpdatedAt(LocalDateTime.now());
        getProvider().update(reverse(data));
        return data;
    }

    public SearchWrapper<D> getAll(Integer from, Integer size) {
        final SearchWrapper<E> results = getProvider().getAll(from, size);
        return convertAll(results);
    }

    public long count() {
        return getProvider().count();
    }


    public void delete(D dto, String deletedBy) {
        KnowledgeSystemLogger.warning(this.getClass(), "User '{}' is deleting '{}'.", deletedBy, dto);
        getProvider().delete(reverse(dto));
    }


    public void delete(UUID id, String deletedBy) {
        KnowledgeSystemLogger.warning(this.getClass(), "User '{}' is deleting '{}'.", deletedBy, id);
        getProvider().delete(id);
    }
}
