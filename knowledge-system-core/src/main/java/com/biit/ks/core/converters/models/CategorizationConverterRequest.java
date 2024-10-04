package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.Categorization;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Optional;

public class CategorizationConverterRequest extends ConverterRequest<Categorization> {

    public CategorizationConverterRequest(Categorization entity) {
        super(entity);
    }

    public CategorizationConverterRequest(Optional<Categorization> entity) {
        super(entity);
    }
}
