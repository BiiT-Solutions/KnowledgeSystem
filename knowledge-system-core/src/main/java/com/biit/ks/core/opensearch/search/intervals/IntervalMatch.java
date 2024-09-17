package com.biit.ks.core.opensearch.search.intervals;

public class IntervalMatch extends IntervalField {
    private String query;
    private Integer maxGap;

    public IntervalMatch() {
        super();
    }

    public IntervalMatch(String field, String query) {
        this();
        setField(field);
        this.query = query;
    }

    public IntervalMatch(String field, String query, Integer maxGap) {
        this();
        setField(field);
        setQuery(query);
        setMaxGap(maxGap);
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getMaxGap() {
        return maxGap;
    }

    public void setMaxGap(Integer maxGap) {
        this.maxGap = maxGap;
    }
}
