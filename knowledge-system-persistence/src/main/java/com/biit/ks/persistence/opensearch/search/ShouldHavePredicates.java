package com.biit.ks.persistence.opensearch.search;

public class ShouldHavePredicates extends SearchPredicates {

    private Integer minimumShouldMatch;

    public Integer getMinimumShouldMatch() {
        return minimumShouldMatch;
    }

    public void setMinimumShouldMatch(Integer minimumShouldMatch) {
        this.minimumShouldMatch = minimumShouldMatch;
    }
}
