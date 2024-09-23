package com.biit.ks.core.controllers;


import com.biit.ks.core.converters.FormRulesLinkConverter;
import com.biit.ks.core.converters.models.FormRulesLinkConverterRequest;
import com.biit.ks.core.models.FormRulesLinkDTO;
import com.biit.ks.core.providers.FormRulesLinkProvider;
import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.server.controller.SimpleController;
import com.biit.server.logger.DtoControllerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

@Controller
public class FormRulesLinkController extends SimpleController<FormRulesLink, FormRulesLinkDTO, FormRulesLinkProvider,
        FormRulesLinkConverterRequest, FormRulesLinkConverter> {

    @Autowired
    protected FormRulesLinkController(FormRulesLinkProvider provider, FormRulesLinkConverter converter) {
        super(provider, converter);
    }

    @Override
    protected FormRulesLinkConverterRequest createConverterRequest(FormRulesLink entity) {
        return new FormRulesLinkConverterRequest(entity);
    }

    @Override
    public FormRulesLinkDTO create(FormRulesLinkDTO dto, String creatorName) {
        if (dto.getCreatedBy() == null && creatorName != null) {
            dto.setCreatedBy(creatorName);
        }
        validate(dto);
        final FormRulesLinkDTO dtoStored = convert(getProvider().save(getConverter().reverse(dto)));
        DtoControllerLogger.info(this.getClass(), "Entity '{}' created by '{}'.", dtoStored, creatorName);
        return dtoStored;
    }

    @Override
    public Collection<FormRulesLinkDTO> create(Collection<FormRulesLinkDTO> formDTOS, String creatorName) {
        return List.of();
    }
}
