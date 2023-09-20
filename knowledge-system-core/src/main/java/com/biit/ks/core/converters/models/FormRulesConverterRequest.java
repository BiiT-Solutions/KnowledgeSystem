package com.biit.ks.core.converters.models;

import com.biit.ks.persistence.entities.FormRules;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Optional;

public class FormRulesConverterRequest extends ConverterRequest<FormRules> {
    public FormRulesConverterRequest(FormRules entity) {
        super(entity);
    }

    public FormRulesConverterRequest(Optional<FormRules> entity) {
        super(entity);
    }
}
