package com.biit.ks.core.providers;


import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.ks.persistence.repositories.FormRulesLinkRepository;
import org.springframework.stereotype.Service;

@Service
public class FormRulesLinkProvider {

    private final FormRulesLinkRepository formRulesLinkRepository;

    public FormRulesLinkProvider(FormRulesLinkRepository formRulesLinkRepository) {
        this.formRulesLinkRepository = formRulesLinkRepository;
    }

    private FormRulesLinkRepository getRepository() {
        return formRulesLinkRepository;
    }

    public FormRulesLink save(FormRulesLink formRulesLink) {
        return null;
    }
}
