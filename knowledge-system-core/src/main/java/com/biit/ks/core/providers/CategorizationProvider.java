package com.biit.ks.core.providers;

import com.biit.ks.core.providers.pools.OpenSearchElementPool;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategorizationProvider extends OpenSearchElementProvider<Categorization, CategorizationRepository> {

    private final CategorizationRepository categorizationRepository;

    public CategorizationProvider(OpenSearchElementPool<Categorization> openSearchElementPool, CategorizationRepository categorizationRepository) {
        super(openSearchElementPool, categorizationRepository);
        this.categorizationRepository = categorizationRepository;
    }

    public Optional<Categorization> get(String categorization) {
        return categorizationRepository.get(categorization);
    }

}
