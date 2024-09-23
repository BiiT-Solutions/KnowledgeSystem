package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FormRulesLinkController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form-rules-link")
public class FormRulesLinkServices {

    private final FormRulesLinkController formRulesLinkController;

    public FormRulesLinkServices(FormRulesLinkController formRulesLinkController) {
        this.formRulesLinkController = formRulesLinkController;
    }
}
