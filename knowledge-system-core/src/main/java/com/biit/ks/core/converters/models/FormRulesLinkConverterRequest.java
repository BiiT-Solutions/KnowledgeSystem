package com.biit.ks.core.converters.models;



import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Optional;

public class FormRulesLinkConverterRequest extends ConverterRequest<FormRulesLink> {
    public FormRulesLinkConverterRequest(FormRulesLink entity) {
        super(entity);
    }

    public FormRulesLinkConverterRequest(Optional<FormRulesLink> entity) {
        super(entity);
    }
}
