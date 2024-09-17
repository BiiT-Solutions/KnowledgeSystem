package com.biit.ks.core.opensearch.search.intervals;

public class IntervalField {
    private String field;

    public IntervalField() {
        super();
    }

    public IntervalField(String field) {
        this();
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
