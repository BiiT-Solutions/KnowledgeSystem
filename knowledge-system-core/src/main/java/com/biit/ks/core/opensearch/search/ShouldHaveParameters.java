package com.biit.ks.core.opensearch.search;

public class ShouldHaveParameters extends SearchParameters {

    private Integer minimumShouldMatch;

    public Integer getMinimumShouldMatch() {
        return minimumShouldMatch;
    }

    public void setMinimumShouldMatch(Integer minimumShouldMatch) {
        this.minimumShouldMatch = minimumShouldMatch;
    }
}
