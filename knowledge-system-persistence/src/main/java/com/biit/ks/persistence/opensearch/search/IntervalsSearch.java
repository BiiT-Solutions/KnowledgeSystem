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

import com.biit.ks.persistence.opensearch.search.intervals.IntervalMatch;
import com.biit.ks.persistence.opensearch.search.intervals.IntervalSearchPrefix;
import com.biit.ks.persistence.opensearch.search.intervals.IntervalWildcards;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;

import java.util.ArrayList;
import java.util.List;

public class IntervalsSearch {
    private final List<IntervalSearchPrefix> prefixes;
    private final List<IntervalMatch> matches;
    private final List<IntervalWildcards> wildcards;
    private QuantifiersOperator quantifiersOperator;

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

    public void addMatch(String field, String query, Integer maxGap, Boolean ordered) {
        addMatch(new IntervalMatch(field, query, maxGap, ordered));
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

    public QuantifiersOperator getIntervalsSearchOperator() {
        return quantifiersOperator;
    }

    public void setIntervalsSearchOperator(QuantifiersOperator quantifiersOperator) {
        this.quantifiersOperator = quantifiersOperator;
    }

    public String getAnyField() {
        for (IntervalSearchPrefix intervalSearchPrefix : prefixes) {
            return intervalSearchPrefix.getField();
        }
        for (IntervalMatch intervalMatch : matches) {
            return intervalMatch.getField();
        }
        for (IntervalWildcards wildcards : wildcards) {
            return wildcards.getField();
        }
        return null;
    }
}
