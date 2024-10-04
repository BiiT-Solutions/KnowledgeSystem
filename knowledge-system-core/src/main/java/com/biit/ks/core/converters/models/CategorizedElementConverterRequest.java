package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Optional;

public class CategorizedElementConverterRequest<E extends CategorizedElement<?>> extends ConverterRequest<E> {

    public CategorizedElementConverterRequest(E entity) {
        super(entity);
    }

    public CategorizedElementConverterRequest(Optional<E> entity) {
        super(entity);
    }
}
