package com.biit.ks.core.opensearch.search.intervals;

public class IntervalSearchPrefix extends IntervalField {
    private String prefix;

    public IntervalSearchPrefix() {
        super();
    }

    public IntervalSearchPrefix(String field, String prefix) {
        this();
        setField(field);
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
