package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.Form;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface FormRepository extends ElementRepository<Form, Long> {

    List<Form> findByNameOrderByVersionDesc(String name);

    Optional<Form> findByNameAndVersion(String name, Integer version);

    List<Form> findByName(String name);

}
