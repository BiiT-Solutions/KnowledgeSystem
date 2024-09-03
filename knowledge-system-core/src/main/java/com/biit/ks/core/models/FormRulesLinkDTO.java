package com.biit.ks.core.models;

import com.biit.server.controllers.models.ElementDTO;

public class FormRulesLinkDTO extends ElementDTO<Long> {

    private Long id;

    private Long formId;
    private Long rulesId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

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
