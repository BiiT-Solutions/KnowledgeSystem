package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategorizationProvider extends OpenSearchElementProvider<Categorization, CategorizationRepository> {

    private final CategorizationRepository categorizationRepository;

    public CategorizationProvider(CategorizationRepository categorizationRepository) {
        super(categorizationRepository);
        this.categorizationRepository = categorizationRepository;
    }

    public Categorization save(Categorization categorization) {
        return categorizationRepository.save(categorization);
    }

    public Optional<Categorization> get(String categorization) {
        return categorizationRepository.get(categorization);
    }

    public Optional<Categorization> get(UUID uuid) {
        return categorizationRepository.get(uuid);
    }


    public List<Categorization> getAll(Integer from, Integer size) {
        return categorizationRepository.getAll(from, size);
    }
}
