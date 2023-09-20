package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.FormRules;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface FormRulesRepository extends ElementRepository<FormRules, Long> {

    List<FormRules> findByName(String name);

    List<FormRules> findByOrganizationId(String organizationId);

    Optional<FormRules> findByNameAndVersion(String name, Integer version);
}
