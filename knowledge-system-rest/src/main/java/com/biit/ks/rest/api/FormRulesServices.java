package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FormRulesController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form-rules")
public class FormRulesServices {

    private final FormRulesController formRulesController;

    public FormRulesServices(FormRulesController formRulesController) {
        this.formRulesController = formRulesController;
    }
}
