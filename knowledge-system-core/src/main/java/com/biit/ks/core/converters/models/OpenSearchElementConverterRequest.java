package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Optional;

public class OpenSearchElementConverterRequest<E extends OpenSearchElement<?>> extends ConverterRequest<E> {

    public OpenSearchElementConverterRequest(E entity) {
        super(entity);
    }

    public OpenSearchElementConverterRequest(Optional<E> entity) {
        super(entity);
    }
}
