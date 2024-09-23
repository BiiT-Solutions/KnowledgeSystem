package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.Form;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class FormRepository {
    public Optional<Form> findById(Long id) {
        return Optional.empty();
    }

    public List<Form> findAll() {
        return null;
    }

    public Collection<Form> findByNameOrderByVersionDesc(String name) {
        return null;
    }

    public Optional<Form> findByNameAndVersion(String name, Integer version) {
        return Optional.empty();
    }

    public Form save(Form form) {
        return null;
    }
}
