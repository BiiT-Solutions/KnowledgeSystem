package com.biit.ks.core.opensearch.search;

import org.springframework.data.util.Pair;

import java.util.List;

public class ShouldHaveParameters extends SearchParameters {

    private Integer minimumShouldMatch;

    public ShouldHaveParameters() {
        this(null);
    }

    public ShouldHaveParameters(List<Pair<String, String>> search) {
        this(search, null);
    }

    public ShouldHaveParameters(List<Pair<String, String>> search, Integer minimumShouldMatch) {
        super(search);
        this.minimumShouldMatch = minimumShouldMatch;
    }

    public Integer getMinimumShouldMatch() {
        return minimumShouldMatch;
    }

    public void setMinimumShouldMatch(Integer minimumShouldMatch) {
        this.minimumShouldMatch = minimumShouldMatch;
    }
}
