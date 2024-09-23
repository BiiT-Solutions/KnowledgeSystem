package com.biit.ks.persistence.entities;

public class FormRulesLink extends Element<Long> {


    private Long id;


    private Form form;

    private FormRules formRules;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public FormRules getFormRules() {
        return this.formRules;
    }

    public void setFormRules(FormRules formRules) {
        this.formRules = formRules;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
