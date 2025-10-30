package com.biit.ks.persistence.opensearch;

/*-
 * #%L
 * Knowledge System (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.ks.persistence.opensearch.search.IntervalsSearch;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.biit.ks.persistence.opensearch.search.MustNotHavePredicates;
import com.biit.ks.persistence.opensearch.search.SearchFilter;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;

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

    private SortResultOptions sortResultOptions;

    //Pagination to restrict the number of results.
    private Integer from;
    private Integer size;

    public SearchQuery(Class<I> dataClass) {
        super();
        this.dataClass = dataClass;
    }

    public SearchQuery(Class<I> dataClass, SortResultOptions sortResultOptions, SearchPredicates... searchParameters) {
        this(dataClass);
        this.sortResultOptions = sortResultOptions;
        this.searchParameters = Set.of(searchParameters);
    }

    public SearchQuery(Class<I> dataClass, SearchPredicates... searchParameters) {
        this(dataClass);
        this.searchParameters = Set.of(searchParameters);
    }

    public SearchQuery(Class<I> dataClass, SortResultOptions sortResultOptions, Integer from, Integer size, SearchPredicates... searchParameters) {
        this(dataClass);
        this.searchParameters = Set.of(searchParameters);
        this.sortResultOptions = sortResultOptions;
        setFrom(from);
        setSize(size);
    }

    public SearchQuery(Class<I> dataClass, Integer from, Integer size, SearchPredicates... searchParameters) {
        this(dataClass);
        this.searchParameters = Set.of(searchParameters);
        setFrom(from);
        setSize(size);
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

    public SearchQuery(Class<I> dataClass, Integer from, Integer size, IntervalsSearch intervals, SearchPredicates... searchParameters) {
        this(dataClass);
        this.searchParameters = Set.of(searchParameters);
        this.intervals = intervals;
        setFrom(from);
        setSize(size);
    }

    public SearchQuery(Class<I> dataClass, Integer from, Integer size) {
        this(dataClass);
        setFrom(from);
        setSize(size);
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

    public SortResultOptions getSortResultOptions() {
        return sortResultOptions;
    }

    public void setSortResultOptions(SortResultOptions sortResultOptions) {
        this.sortResultOptions = sortResultOptions;
    }
}
