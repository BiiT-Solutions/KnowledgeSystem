package com.biit.ks.persistence.opensearch.search.intervals;

public class IntervalWildcards extends IntervalField {
    private String pattern;

    public IntervalWildcards() {
        super();
    }

    public IntervalWildcards(String field, String pattern) {
        this();
        setField(field);
        this.pattern = pattern;
    }


    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
