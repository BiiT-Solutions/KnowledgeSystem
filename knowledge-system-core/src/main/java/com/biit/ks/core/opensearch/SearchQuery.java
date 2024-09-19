package com.biit.ks.core.opensearch;

import com.biit.ks.core.opensearch.search.IntervalsSearch;
import com.biit.ks.core.opensearch.search.MustHavePredicates;
import com.biit.ks.core.opensearch.search.MustNotHavePredicates;
import com.biit.ks.core.opensearch.search.SearchFilter;
import com.biit.ks.core.opensearch.search.SearchPredicates;
import com.biit.ks.core.opensearch.search.ShouldHavePredicates;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @param <I> class to search.
 */
public class SearchQuery<I> {

    // Type of element to search.
    private final Class<I> dataClass;

    // Pair of parameters-values that must be present, not allowed or are possible to have and any final filter to restrict even more the results.
    private Set<SearchPredicates> searchParameters;

    // Search by words spread in a text.
    private IntervalsSearch intervals;

    //How many should predicate must be present.
    private Integer minimumShouldMatch;

    //Pagination to restrict the number of results.
    private Integer from;
    private Integer size;

    public SearchQuery(Class<I> dataClass) {
        super();
        this.dataClass = dataClass;
    }

    public SearchQuery(Class<I> dataClass, SearchPredicates... searchParameters) {
        this(dataClass);
        this.searchParameters = Set.of(searchParameters);
    }

    public SearchQuery(Class<I> dataClass, IntervalsSearch intervals) {
        this(dataClass);
        this.intervals = intervals;
    }

    public SearchQuery(Class<I> dataClass, IntervalsSearch intervals, SearchPredicates... searchParameters) {
        this(dataClass);
        this.searchParameters = Set.of(searchParameters);
        this.intervals = intervals;
    }

    public Class<I> getDataClass() {
        return dataClass;
    }

    public Set<SearchPredicates> getSearchParameters() {
        return searchParameters;
    }

    public void setSearchParameters(Set<SearchPredicates> searchParameters) {
        this.searchParameters = searchParameters;
    }

    public IntervalsSearch getIntervals() {
        return intervals;
    }

    public void setIntervals(IntervalsSearch intervals) {
        this.intervals = intervals;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Set<MustHavePredicates> getMustHaveValues() {
        if (searchParameters == null) {
            return new HashSet<>();
        }
        return searchParameters.stream().filter(p -> p instanceof MustHavePredicates).map(p -> (MustHavePredicates) p).collect(Collectors.toSet());
    }

    public Set<MustNotHavePredicates> getMustNotHaveValues() {
        if (searchParameters == null) {
            return new HashSet<>();
        }
        return searchParameters.stream().filter(p -> p instanceof MustNotHavePredicates).map(p -> (MustNotHavePredicates) p).collect(Collectors.toSet());
    }

    public Set<ShouldHavePredicates> getShouldHaveValues() {
        if (searchParameters == null) {
            return new HashSet<>();
        }
        return searchParameters.stream().filter(p -> p instanceof ShouldHavePredicates).map(p -> (ShouldHavePredicates) p).collect(Collectors.toSet());
    }

    public Set<SearchFilter> getFilters() {
        if (searchParameters == null) {
            return new HashSet<>();
        }
        return searchParameters.stream().filter(p -> p instanceof SearchFilter).map(p -> (SearchFilter) p).collect(Collectors.toSet());
    }

    public Integer getMinimumShouldMatch() {
        if (minimumShouldMatch != null) {
            return minimumShouldMatch;
        }
        final Optional<ShouldHavePredicates> minimumShouldMatch = getShouldHaveValues().stream()
                .max(Comparator.comparing(ShouldHavePredicates::getMinimumShouldMatch));
        return minimumShouldMatch.map(ShouldHavePredicates::getMinimumShouldMatch).orElse(null);
    }

    public void setMinimumShouldMatch(Integer minimumShouldMatch) {
        this.minimumShouldMatch = minimumShouldMatch;
    }
}
