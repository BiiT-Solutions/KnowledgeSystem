package com.biit.ks.core.providers;

import com.biit.ks.core.exceptions.CategoryAlreadyExistsException;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.opensearch.search.ResponseWrapper;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorizationProvider extends OpenSearchElementProvider<Categorization, CategorizationRepository> {

    private final CategorizationRepository categorizationRepository;

    public CategorizationProvider(CategorizationRepository categorizationRepository) {
        super(categorizationRepository);
        this.categorizationRepository = categorizationRepository;
    }

    //Name is unique on categories
    public ResponseWrapper<Categorization> get(String name) {
        return getRepository().get(name);
    }

    public ResponseWrapper<Categorization> get(List<String> categorizations) {
        return getRepository().get(categorizations);
    }

    public ResponseWrapper<Categorization> create(String categorization, String creatorName) {
        return create(new Categorization(categorization), creatorName);
    }

    public ResponseWrapper<Categorization> create(Categorization element, String creatorName) {
        if (element.getCreatedBy() == null && creatorName != null) {
            element.setCreatedBy(creatorName);
        }
        if (!get(element.getName()).isEmpty()) {
            throw new CategoryAlreadyExistsException(this.getClass(), "Already exists a category with name " + element.getName());
        }
        final Categorization stored = save(element);
        KnowledgeSystemLogger.info(this.getClass(), "Entity '{}' created by '{}'.", stored, creatorName);
        return new ResponseWrapper<>(stored);
    }

}
