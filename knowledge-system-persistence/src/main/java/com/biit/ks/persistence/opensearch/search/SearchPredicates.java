package com.biit.ks.persistence.opensearch.search;

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

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchPredicates {
    private final List<Pair<String, String>> search;
    //Categories are always an exact match.
    private final List<Pair<String, String>> categories;
    //For searching one element on multiples fields.
    private final List<Pair<List<String>, String>> multiSearch;
    private final List<Range> ranges;
    private FuzzinessDefinition fuzzinessDefinition;

    public SearchPredicates() {
        this.search = new ArrayList<>();
        this.categories = new ArrayList<>();
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

    public void addCategory(Pair<String, String> pair) {
        categories.add(pair);
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

    public List<Pair<String, String>> getCategories() {
        return categories;
    }

    public FuzzinessDefinition getFuzzinessDefinition() {
        return fuzzinessDefinition;
    }

    public void setFuzzinessDefinition(FuzzinessDefinition fuzzinessDefinition) {
        this.fuzzinessDefinition = fuzzinessDefinition;
    }
}
