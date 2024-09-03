package com.biit.ks.core.controllers;


import com.biit.ks.core.converters.FormRulesLinkConverter;
import com.biit.ks.core.converters.models.FormRulesLinkConverterRequest;
import com.biit.ks.core.models.FormRulesLinkDTO;
import com.biit.ks.core.providers.FormRulesLinkProvider;
import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.ks.persistence.repositories.FormRulesLinkRepository;
import com.biit.server.controller.ElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class FormRulesLinkController extends ElementController<FormRulesLink, Long, FormRulesLinkDTO, FormRulesLinkRepository,
        FormRulesLinkProvider, FormRulesLinkConverterRequest, FormRulesLinkConverter> {

    @Autowired
    protected FormRulesLinkController(FormRulesLinkProvider provider, FormRulesLinkConverter converter) {
        super(provider, converter);
    }

    @Override
    protected FormRulesLinkConverterRequest createConverterRequest(FormRulesLink entity) {
        return new FormRulesLinkConverterRequest(entity);
    }
}
