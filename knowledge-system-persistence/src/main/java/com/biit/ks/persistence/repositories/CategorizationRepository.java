package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import jakarta.annotation.PostConstruct;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategorizationRepository extends OpenSearchElementRepository<Categorization> {

    public static final String OPENSEARCH_INDEX = "categorizations";

    public CategorizationRepository(OpenSearchClient openSearchClient) {
        super(Categorization.class, openSearchClient);
    }

    @PostConstruct
    public void createIndex() {
        try {
            getOpenSearchClient().createIndex(OPENSEARCH_INDEX);
        } catch (OpenSearchException e) {
            if (!e.getMessage().contains("resource_already_exists_exception")) {
                throw e;
            }
        }
    }

    @Override
    public String getOpenSearchIndex() {
        return OPENSEARCH_INDEX;
    }

    public Categorization save(Categorization categorization) {
        getOpenSearchClient().indexData(categorization, OPENSEARCH_INDEX, categorization.getUuid() != null ? categorization.getUuid().toString() : null);
        return categorization;
    }

    public Optional<Categorization> get(String name) {
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        mustHavePredicates.add(Pair.of("name", name));
        final SearchResponse<Categorization> response = getOpenSearchClient().searchData(Categorization.class, mustHavePredicates);
        final List<Categorization> categorizations = getOpenSearchClient().convertResponse(response);
        if (categorizations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(categorizations.get(categorizations.size() - 1));
    }


    public Optional<Categorization> get(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        final GetResponse<Categorization> response = getOpenSearchClient().getData(Categorization.class, OPENSEARCH_INDEX, uuid.toString());
        if (response == null || response.source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.source());
    }

    public List<Categorization> getAll(Integer from, Integer size) {
        final SearchResponse<Categorization> response = getOpenSearchClient().getAll(Categorization.class, OPENSEARCH_INDEX, from, size);
        return getOpenSearchClient().convertResponse(response);
    }

}
