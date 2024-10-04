package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.CategorizedElement;

import java.util.Optional;

public class CategorizedElementConverterRequest<E extends CategorizedElement<?>> extends OpenSearchElementConverterRequest<E> {

    public CategorizedElementConverterRequest(E entity) {
        super(entity);
    }

    public CategorizedElementConverterRequest(Optional<E> entity) {
        super(entity);
    }
}
