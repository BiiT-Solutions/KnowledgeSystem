package com.biit.ks.core.providers;

import com.biit.ks.core.exceptions.CategoryAlreadyExistsException;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorizationProvider extends OpenSearchElementProvider<Categorization, CategorizationRepository> {

    private final CategorizationRepository categorizationRepository;

    public CategorizationProvider(CategorizationRepository categorizationRepository) {
        super(categorizationRepository);
        this.categorizationRepository = categorizationRepository;
    }

    //Name is unique on categories
    public Optional<Categorization> get(String name) {
        final List<Categorization> categorizations = getRepository().get(name);
        if (categorizations.isEmpty()) {
            return Optional.empty();
        }
        return categorizations.stream().findFirst();
    }

    public List<Categorization> get(List<String> categorizations) {
        return getRepository().get(categorizations);
    }

    public Categorization create(String categorization, String creatorName) {
        return create(new Categorization(categorization), creatorName);
    }

    public Categorization create(Categorization element, String creatorName) {
        if (element.getCreatedBy() == null && creatorName != null) {
            element.setCreatedBy(creatorName);
        }
        if (get(element.getName()).isPresent()) {
            throw new CategoryAlreadyExistsException(this.getClass(), "Already exists a category with name " + element.getName());
        }
        final Categorization stored = save(element);
        KnowledgeSystemLogger.info(this.getClass(), "Entity '{}' created by '{}'.", stored, creatorName);
        return stored;
    }

}
