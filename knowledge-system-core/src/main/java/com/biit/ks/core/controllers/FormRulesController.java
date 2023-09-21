package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.FormRulesConverter;
import com.biit.ks.core.converters.models.FormRulesConverterRequest;
import com.biit.ks.core.models.FormRulesDTO;
import com.biit.ks.core.providers.FormRulesProvider;
import com.biit.ks.persistence.entities.FormRules;
import com.biit.ks.persistence.repositories.FormRulesRepository;
import com.biit.server.controller.BasicElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FormRulesController extends BasicElementController<FormRules, FormRulesDTO, FormRulesRepository,
        FormRulesProvider, FormRulesConverterRequest, FormRulesConverter> {

    @Autowired
    protected FormRulesController(FormRulesProvider provider, FormRulesConverter converter) {
        super(provider, converter);
    }

    @Override
    protected FormRulesConverterRequest createConverterRequest(FormRules entity) {
        return new FormRulesConverterRequest(entity);
    }
}
