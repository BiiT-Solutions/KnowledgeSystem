package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategorizationRepository extends OpenSearchElementRepository<Categorization> {

    private final OpenSearchConfigurator openSearchConfigurator;

    public CategorizationRepository(OpenSearchClient openSearchClient, OpenSearchConfigurator openSearchConfigurator) {
        super(Categorization.class, openSearchClient);
        this.openSearchConfigurator = openSearchConfigurator;
    }

    @Override
    public String getOpenSearchIndex() {
        return openSearchConfigurator.getOpenSearchCategorizationsIndex();
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


    @Override
    public List<Categorization> search(String query, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("name", query));
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        final SearchResponse<Categorization> response = getOpenSearchClient().searchData(Categorization.class, shouldHavePredicates, from, size);
        return getOpenSearchClient().convertResponse(response);
    }


    public List<Categorization> getAll(Integer from, Integer size) {
        final SearchResponse<Categorization> response = getOpenSearchClient().getAll(Categorization.class, getOpenSearchIndex(), from, size);
        return getOpenSearchClient().convertResponse(response);
    }

}
