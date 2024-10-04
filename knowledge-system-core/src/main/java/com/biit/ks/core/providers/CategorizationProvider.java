package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategorizationProvider {

    private final CategorizationRepository fileEntryRepository;

    public CategorizationProvider(CategorizationRepository fileEntryRepository) {
        this.fileEntryRepository = fileEntryRepository;
    }

    public Categorization save(Categorization categorization) {
        return fileEntryRepository.save(categorization);
    }

    public Optional<Categorization> get(String categorization) {
        return fileEntryRepository.get(categorization);
    }

    public Optional<Categorization> get(UUID uuid) {
        return fileEntryRepository.get(uuid);
    }


    public List<Categorization> getAll() {
        return fileEntryRepository.getAll();
    }
}
