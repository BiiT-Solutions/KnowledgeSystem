package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FormRulesController;
import com.biit.ks.core.converters.FormRulesConverter;
import com.biit.ks.core.converters.models.FormRulesConverterRequest;
import com.biit.ks.core.models.FormRulesDTO;
import com.biit.ks.core.providers.FormRulesProvider;
import com.biit.ks.persistence.entities.FormRules;
import com.biit.ks.persistence.repositories.FormRulesRepository;
import com.biit.server.rest.ElementServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form-rules")
public class FormRulesServices extends ElementServices<FormRules, Long, FormRulesDTO, FormRulesRepository,
        FormRulesProvider, FormRulesConverterRequest, FormRulesConverter, FormRulesController> {

    @Autowired
    public FormRulesServices(FormRulesController formRulesController) {
        super(formRulesController);
    }

}
