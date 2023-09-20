package com.biit.ks.core.converters;

import com.biit.ks.core.converters.models.FormRulesLinkConverterRequest;
import com.biit.ks.core.models.FormRulesLinkDTO;
import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;

public class FormRulesLinkConverter extends ElementConverter<FormRulesLink, FormRulesLinkDTO, FormRulesLinkConverterRequest> {

    @Override
    protected FormRulesLinkDTO convertElement(FormRulesLinkConverterRequest from) {
        final FormRulesLinkDTO applicationDTO = new FormRulesLinkDTO();
        BeanUtils.copyProperties(from.getEntity(), applicationDTO);
        return applicationDTO;
    }

    @Override
    public FormRulesLink reverse(FormRulesLinkDTO to) {
        if (to == null) {
            return null;
        }
        final FormRulesLink application = new FormRulesLink();
        BeanUtils.copyProperties(to, application);
        return application;
    }
}
