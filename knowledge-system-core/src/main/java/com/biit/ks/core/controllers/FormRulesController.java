package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.FormRulesConverter;
import com.biit.ks.core.converters.models.FormRulesConverterRequest;
import com.biit.ks.core.models.FormRulesDTO;
import com.biit.ks.core.providers.FormRulesProvider;
import com.biit.ks.persistence.entities.FormRules;
import com.biit.server.controller.SimpleController;
import com.biit.server.logger.DtoControllerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

@Controller
public class FormRulesController extends SimpleController<FormRules, FormRulesDTO, FormRulesProvider,
        FormRulesConverterRequest, FormRulesConverter> {

    @Autowired
    protected FormRulesController(FormRulesProvider provider, FormRulesConverter converter) {
        super(provider, converter);
    }

    @Override
    protected FormRulesConverterRequest createConverterRequest(FormRules entity) {
        return new FormRulesConverterRequest(entity);
    }

    @Override
    public FormRulesDTO create(FormRulesDTO dto, String creatorName) {
        if (dto.getCreatedBy() == null && creatorName != null) {
            dto.setCreatedBy(creatorName);
        }
        validate(dto);
        final FormRulesDTO dtoStored = convert(getProvider().save(getConverter().reverse(dto)));
        DtoControllerLogger.info(this.getClass(), "Entity '{}' created by '{}'.", dtoStored, creatorName);
        return dtoStored;
    }

    @Override
    public Collection<FormRulesDTO> create(Collection<FormRulesDTO> formDTOS, String creatorName) {
        return List.of();
    }
}
