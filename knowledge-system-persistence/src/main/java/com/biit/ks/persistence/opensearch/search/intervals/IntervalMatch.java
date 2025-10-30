package com.biit.ks.persistence.opensearch.search.intervals;

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
