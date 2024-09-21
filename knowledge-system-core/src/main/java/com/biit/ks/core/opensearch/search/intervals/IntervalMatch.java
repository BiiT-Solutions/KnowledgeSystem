package com.biit.ks.core.opensearch.search.intervals;

public class IntervalMatch extends IntervalField {
    private String query;
    //Existing words between matching elements.
    private Integer maxGap;
    //If the matching elements must appear on the same order.
    private Boolean ordered;

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

    public IntervalMatch(String field, String query, Integer maxGap, Boolean ordered) {
        this();
        setField(field);
        setQuery(query);
        setMaxGap(maxGap);
        setOrdered(ordered);
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

    public Boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(Boolean ordered) {
        this.ordered = ordered;
    }
}
