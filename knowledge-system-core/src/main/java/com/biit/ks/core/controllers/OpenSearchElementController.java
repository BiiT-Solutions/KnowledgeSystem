package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.OpenSearchElementConverter;
import com.biit.ks.core.converters.models.OpenSearchElementConverterRequest;
import com.biit.ks.core.models.OpenSearchElementDTO;
import com.biit.ks.core.providers.OpenSearchElementProvider;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;
import com.biit.server.controller.SimpleController;

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
}
