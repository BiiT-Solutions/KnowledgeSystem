package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FormRulesLinkController;
import com.biit.ks.core.converters.FormRulesLinkConverter;
import com.biit.ks.core.converters.models.FormRulesLinkConverterRequest;
import com.biit.ks.core.models.FormRulesLinkDTO;
import com.biit.ks.core.providers.FormRulesLinkProvider;
import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.ks.persistence.repositories.FormRulesLinkRepository;
import com.biit.server.rest.ElementServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form-rules-link")
public class FormRulesLinkServices extends ElementServices<FormRulesLink, Long, FormRulesLinkDTO, FormRulesLinkRepository,
        FormRulesLinkProvider, FormRulesLinkConverterRequest, FormRulesLinkConverter, FormRulesLinkController> {

    public FormRulesLinkServices(FormRulesLinkController formRulesLinkController) {
        super(formRulesLinkController);
    }
}
