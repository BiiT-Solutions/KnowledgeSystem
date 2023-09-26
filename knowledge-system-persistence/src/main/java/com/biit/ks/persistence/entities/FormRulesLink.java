package com.biit.ks.persistence.entities;

import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.context.annotation.Primary;



@Entity
@Primary
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "form_rules_link")
public class FormRulesLink extends Element {

    @ManyToOne
    @JoinColumn(name = "form")
    private Form form;

    @ManyToOne
    @JoinColumn(name = "form_rules")
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
}
