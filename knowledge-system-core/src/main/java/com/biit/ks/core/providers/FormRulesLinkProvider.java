package com.biit.ks.core.providers;


import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.ks.persistence.repositories.FormRulesLinkRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

@Service
public class FormRulesLinkProvider extends ElementProvider<FormRulesLink, Long, FormRulesLinkRepository> {


    public FormRulesLinkProvider(FormRulesLinkRepository repository) {
        super(repository);
    }
}
