package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategorizationRepository extends OpenSearchElementRepository<Categorization> {

    private final IOpenSearchConfigurator openSearchConfigurator;

    public CategorizationRepository(OpenSearchClient openSearchClient, IOpenSearchConfigurator openSearchConfigurator) {
        super(Categorization.class, openSearchClient);
        this.openSearchConfigurator = openSearchConfigurator;
    }

    @Override
    public String getOpenSearchIndex() {
        return openSearchConfigurator.getOpenSearchCategorizationsIndex();
    }


    public SearchWrapper<Categorization> get(List<String> names) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        names.forEach(name -> shouldHavePredicates.add(Pair.of("name", name)));
        shouldHavePredicates.setMinimumShouldMatch(1);
        final SearchResponse<Categorization> response = getOpenSearchClient().searchData(Categorization.class, getOpenSearchIndex(), shouldHavePredicates);
        return getOpenSearchClient().convertResponse(response);
    }


    @Override
    public SearchPredicates searchByValuePredicate(String value, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("name", value));
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        return shouldHavePredicates;
    }

}
