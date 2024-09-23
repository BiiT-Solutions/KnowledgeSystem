package com.biit.ks.core.controllers;


import com.biit.ks.core.converters.FormConverter;
import com.biit.ks.core.converters.models.FormConverterRequest;
import com.biit.ks.core.models.FormDTO;
import com.biit.ks.core.providers.FormProvider;
import com.biit.ks.persistence.entities.Form;
import com.biit.server.controller.SimpleController;
import com.biit.server.logger.DtoControllerLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

@Controller
public class FormController extends SimpleController<Form, FormDTO, FormProvider,
        FormConverterRequest, FormConverter> {

    @Autowired
    protected FormController(FormProvider provider, FormConverter converter) {
        super(provider, converter);
    }

    @Override
    protected FormConverterRequest createConverterRequest(Form entity) {
        return new FormConverterRequest(entity);
    }

    @Override
    public FormDTO create(FormDTO dto, String creatorName) {
        if (dto.getCreatedBy() == null && creatorName != null) {
            dto.setCreatedBy(creatorName);
        }
        validate(dto);
        final FormDTO dtoStored = convert(getProvider().save(getConverter().reverse(dto)));
        DtoControllerLogger.info(this.getClass(), "Entity '{}' created by '{}'.", dtoStored, creatorName);
        return dtoStored;
    }

    @Override
    public Collection<FormDTO> create(Collection<FormDTO> formDTOS, String creatorName) {
        return List.of();
    }

    public FormDTO getByName(String name, Integer version) {
        return getConverter().convert(new FormConverterRequest(getProvider().getByName(name, version).orElse(null)));
    }
}
