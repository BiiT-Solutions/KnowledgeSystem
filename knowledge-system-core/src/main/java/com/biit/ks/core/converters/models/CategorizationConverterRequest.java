package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.Categorization;

import java.util.Optional;

public class CategorizationConverterRequest extends OpenSearchElementConverterRequest<Categorization> {

    public CategorizationConverterRequest(Categorization entity) {
        super(entity);
    }

    public CategorizationConverterRequest(Optional<Categorization> entity) {
        super(entity);
    }
}
