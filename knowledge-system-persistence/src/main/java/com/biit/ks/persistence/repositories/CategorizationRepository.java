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
public class CategorizationRepository {

    public static final String OPENSEARCH_INDEX = "categorizations";

    private final OpenSearchClient openSearchClient;

    public CategorizationRepository(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    @PostConstruct
    public void createIndex() {
        try {
            openSearchClient.createIndex(OPENSEARCH_INDEX);
        } catch (OpenSearchException e) {
            if (!e.getMessage().contains("resource_already_exists_exception")) {
                throw e;
            }
        }
    }

    public Categorization save(Categorization categorization) {
        openSearchClient.indexData(categorization, OPENSEARCH_INDEX, categorization.getUuid() != null ? categorization.getUuid().toString() : null);
        return categorization;
    }

    public Optional<Categorization> get(String name) {
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        mustHavePredicates.add(Pair.of("name", name));
        final SearchResponse<Categorization> response = openSearchClient.searchData(Categorization.class, mustHavePredicates);
        final List<Categorization> categorizations = openSearchClient.convertResponse(response);
        if (categorizations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(categorizations.get(categorizations.size() - 1));
    }


    public Optional<Categorization> get(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        final GetResponse<Categorization> response = openSearchClient.getData(Categorization.class, OPENSEARCH_INDEX, uuid.toString());
        if (response == null || response.source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.source());
    }

    public List<Categorization> getAll() {
        final SearchResponse<Categorization> response = openSearchClient.getAll(Categorization.class, OPENSEARCH_INDEX);
        return openSearchClient.convertResponse(response);
    }

}
