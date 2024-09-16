package com.biit.ks.core.opensearch.search;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class SearchParameters {
    private final List<Pair<String, String>> search;

    public SearchParameters() {
        this(null);
    }

    public SearchParameters(List<Pair<String, String>> search) {
        this.search = Objects.requireNonNullElseGet(search, ArrayList::new);
    }

    public List<Pair<String, String>> getSearch() {
        return search;
    }

    public void add(Pair<String, String> pair) {
        search.add(pair);
    }

    public void add(String parameter, String value) {
        add(Pair.of(parameter, value));
    }
}
