package com.biit.ks.core.models;

import com.biit.server.controllers.models.ElementDTO;

public class FormRulesLinkDTO extends ElementDTO {

    private Long formId;
    private Long rulesId;

    public Long getFormId() {
        return this.formId;
    }

    public Long getRulesId() {
        return this.rulesId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public void setRulesId(Long rulesId) {
        this.rulesId = rulesId;
    }
}
