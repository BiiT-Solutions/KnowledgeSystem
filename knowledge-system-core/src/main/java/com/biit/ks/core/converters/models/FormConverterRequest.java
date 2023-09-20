package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.Form;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Optional;

public class FormConverterRequest extends ConverterRequest<Form> {

    public FormConverterRequest(Form entity) {
        super(entity);
    }

    public FormConverterRequest(Optional<Form> entity) {
        super(entity);
    }
}
