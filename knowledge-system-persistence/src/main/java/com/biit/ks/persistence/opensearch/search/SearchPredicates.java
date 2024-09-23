package com.biit.ks.persistence.opensearch.search;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchPredicates {
    private final List<Pair<String, String>> search;
    //For searching on multiples fields.
    private final List<Pair<List<String>, String>> multiSearch;
    private final List<Range> ranges;
    private FuzzinessDefinition fuzzinessDefinition;

    public SearchPredicates() {
        this.search = new ArrayList<>();
        this.multiSearch = new ArrayList<>();
        this.ranges = new ArrayList<>();
    }

    public List<Pair<String, String>> getSearch() {
        return search;
    }

    public List<Pair<List<String>, String>> getMultiSearch() {
        return multiSearch;
    }

    public void add(Pair<String, String> pair) {
        search.add(pair);
    }

    public void add(String parameter, String value) {
        add(Pair.of(parameter, value));
    }

    public void addMultiSearch(Pair<List<String>, String> pair) {
        multiSearch.add(pair);
    }

    public void addMultiSearch(List<String> parameters, String value) {
        addMultiSearch(Pair.of(parameters, value));
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void addRange(String parameter, Object from, Object to) {
        ranges.add(new Range(parameter, from, to));
    }

    public FuzzinessDefinition getFuzzinessDefinition() {
        return fuzzinessDefinition;
    }

    public void setFuzzinessDefinition(FuzzinessDefinition fuzzinessDefinition) {
        this.fuzzinessDefinition = fuzzinessDefinition;
    }
}
