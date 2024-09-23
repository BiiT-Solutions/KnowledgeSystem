package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.FormRules;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FormRulesRepository {

    public Optional<FormRules> findByNameAndVersion(String name, Integer version) {
        return Optional.empty();
    }

    public FormRules save(FormRules rulesToStore) {
        return null;
    }

    public List<FormRules> findByOrganizationId(String organizationId) {
        return null;
    }
}
