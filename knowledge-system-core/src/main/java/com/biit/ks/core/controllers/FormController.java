package com.biit.ks.core.controllers;


import com.biit.ks.core.converters.FormConverter;
import com.biit.ks.core.converters.models.FormConverterRequest;
import com.biit.ks.core.models.FormDTO;
import com.biit.ks.core.providers.FormProvider;
import com.biit.ks.persistence.entities.Form;
import com.biit.ks.persistence.repositories.FormRepository;
import com.biit.server.controller.ElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FormController extends ElementController<Form, Long, FormDTO, FormRepository,
        FormProvider, FormConverterRequest, FormConverter> {

    @Autowired
    protected FormController(FormProvider provider, FormConverter converter) {
        super(provider, converter);
    }

    @Override
    protected FormConverterRequest createConverterRequest(Form entity) {
        return new FormConverterRequest(entity);
    }

    public FormDTO getByName(String name, Integer version) {
        return getConverter().convert(new FormConverterRequest(getProvider().getByName(name, version).orElse(null)));
    }
}
