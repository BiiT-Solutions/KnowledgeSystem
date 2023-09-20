package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.FormRulesLink;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public interface FormRulesLinkRepository extends ElementRepository<FormRulesLink, Long> {
}
