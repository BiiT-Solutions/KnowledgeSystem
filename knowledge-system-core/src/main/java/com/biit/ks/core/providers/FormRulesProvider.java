package com.biit.ks.core.providers;

/*-
 * #%L
 * Knowledge System (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.ks.persistence.entities.FormRules;
import com.biit.ks.persistence.repositories.FormRulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FormRulesProvider {

    private final FormRulesRepository formRulesRepository;


    @Autowired
    public FormRulesProvider(FormRulesRepository formRulesRepository) {
        this.formRulesRepository = formRulesRepository;
    }

    private FormRulesRepository getRepository() {
        return formRulesRepository;
    }

    public FormRules save(FormRules reverse) {
        return null;
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
