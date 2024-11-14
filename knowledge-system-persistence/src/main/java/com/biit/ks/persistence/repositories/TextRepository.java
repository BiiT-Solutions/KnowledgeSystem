package com.biit.ks.persistence.repositories;

import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SortOptionOrder;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TextRepository extends CategorizedElementRepository<Text> {

    private final IOpenSearchConfigurator openSearchConfigurator;

    public TextRepository(OpenSearchClient openSearchClient, IOpenSearchConfigurator openSearchConfigurator) {
        super(Text.class, openSearchClient);
        this.openSearchConfigurator = openSearchConfigurator;
    }

    @Override
    public String getOpenSearchIndex() {
        return openSearchConfigurator.getOpenSearchTextIndex();
    }


    public List<Text> search(String query, TextLanguages language, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("description", query));
        shouldHavePredicates.add(Pair.of("name", query));
        shouldHavePredicates.add(Pair.of("content." + language.name(), query));
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        final SearchResponse<Text> response = getOpenSearchClient().searchData(Text.class, getOpenSearchIndex(),
                shouldHavePredicates, new SortResultOptions("createdAt", SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);
    }

    @Override

    public List<Text> search(String query, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("description", query));
        shouldHavePredicates.add(Pair.of("name", query));
        //Add any language here.
        for (TextLanguages language : TextLanguages.values()) {
            shouldHavePredicates.add(Pair.of("content." + language.name(), query));
        }
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        final SearchResponse<Text> response = getOpenSearchClient().searchData(Text.class, getOpenSearchIndex(),
                shouldHavePredicates, new SortResultOptions("createdAt", SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);
    }
}
