package com.biit.ks.core.providers;


import com.biit.ks.persistence.entities.FormRules;
import com.biit.ks.persistence.repositories.FormRulesRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FormRulesProvider extends ElementProvider<FormRules, Long, FormRulesRepository> {

    private final FormProvider formProvider;


    @Autowired
    public FormRulesProvider(FormRulesRepository formRulesRepository, FormProvider formProvider) {
        super(formRulesRepository);
        this.formProvider = formProvider;
    }

    public FormRules addFormRules(String body, String organizationId) {
        final String name = getNameFromJson(body);
        final Integer version = Integer.parseInt(getVersionFromJson(body));
        final String email = getEmailFromJson(body);
        FormRules rulesToStore = getRepository().findByNameAndVersion(name, version).orElse(null);
        if (rulesToStore == null) {
            rulesToStore = new FormRules();
            rulesToStore.setCreatedBy(email);
            rulesToStore.setCreatedAt(LocalDateTime.now());
            rulesToStore.setOrganizationId(organizationId);
        } else {
            rulesToStore.setUpdatedBy(email);
            rulesToStore.setUpdatedAt(LocalDateTime.now());
        }
        rulesToStore.setName(name);
        rulesToStore.setVersion(version);
        rulesToStore.setMetadata(getMetadataFromJson(body));
        rulesToStore.setRules(getRulesFromJson(body));
        return getRepository().save(rulesToStore);
    }


    public List<FormRules> getAllFormRules(String organizationId) {
        return getRepository().findByOrganizationId(organizationId);
    }

    private String getRulesFromJson(String json) {
        return json.split("rules\":")[1].split(",")[0];
    }

    private String getNameFromJson(String json) {
        return json.split("name\":")[1].split(",")[0];
    }

    private String getVersionFromJson(String json) {
        return json.split("version\":")[1].split("}")[0];
    }

    private String getMetadataFromJson(String json) {
        return json.split("metadata\":")[1].split(",")[0];
    }

    private String getEmailFromJson(String json) {
        return json.split("email\":")[1].split(",")[0];
    }
}
