package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.OpenSearchElementConverter;
import com.biit.ks.core.converters.models.OpenSearchElementConverterRequest;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.OpenSearchElementDTO;
import com.biit.ks.core.providers.OpenSearchElementProvider;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;
import com.biit.server.controller.SimpleController;

import java.util.List;
import java.util.UUID;

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

    public List<D> search(String searchQuery, Integer from, Integer size) {
        final List<E> results = getProvider().search(searchQuery, from, size);
        return convertAll(results);
    }

    public D get(UUID uuid) {
        final E fileEntry =
                getProvider().get(uuid).orElseThrow(() -> new FileNotFoundException(this.getClass(), "No element with uuid '" + uuid + "'."));
        return convert(fileEntry);
    }

    public List<D> getAll(Integer from, Integer size) {
        final List<E> results = getProvider().getAll(from, size);
        return convertAll(results);
    }


    public void delete(D dto, String deletedBy) {
        KnowledgeSystemLogger.warning(this.getClass(), "User '{}' is deleting '{}'.", deletedBy, dto);
        getProvider().delete(reverse(dto));
    }
}
