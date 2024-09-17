package com.biit.ks.core.opensearch.search;

import com.biit.ks.core.opensearch.search.intervals.IntervalMatch;
import com.biit.ks.core.opensearch.search.intervals.IntervalSearchPrefix;
import com.biit.ks.core.opensearch.search.intervals.IntervalWildcards;

import java.util.ArrayList;
import java.util.List;

public class IntervalsSearch {
    private final List<IntervalSearchPrefix> prefixes;
    private final List<IntervalMatch> matches;
    private final List<IntervalWildcards> wildcards;

    public IntervalsSearch() {
        prefixes = new ArrayList<>();
        matches = new ArrayList<>();
        wildcards = new ArrayList<>();
    }

    public List<IntervalSearchPrefix> getPrefixes() {
        return prefixes;
    }

    public void addPrefix(IntervalSearchPrefix prefix) {
        prefixes.add(prefix);
    }

    public void addPrefix(String parameter, String prefix) {
        addPrefix(new IntervalSearchPrefix(parameter, prefix));
    }

    public List<IntervalMatch> getMatches() {
        return matches;
    }

    public void addMatch(IntervalMatch match) {
        matches.add(match);
    }

    public void addMatch(String field, String query) {
        addMatch(new IntervalMatch(field, query));
    }

    public void addMatch(String field, String query, Integer maxGap) {
        addMatch(new IntervalMatch(field, query, maxGap));
    }

    public List<IntervalWildcards> getWildcards() {
        return wildcards;
    }

    public void addWildcard(IntervalWildcards wildcard) {
        wildcards.add(wildcard);
    }

    public void addWildcard(String field, String pattern) {
        addWildcard(new IntervalWildcards(field, pattern));
    }
}
