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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class SearchWrapper<T> {
    private final Collection<T> data;
    private long totalElements;

    public SearchWrapper() {
        this(new ArrayList<>());
    }

    public SearchWrapper(Collection<T> data) {
        this.data = data;
        this.totalElements = data.size();
    }

    public SearchWrapper(T data) {
        this();
        this.data.add(data);
        if (data == null) {
            totalElements = 0;
        } else {
            totalElements = 1;
        }
    }

    public Collection<T> getData() {
        return data;
    }

    public T getFirst() {
        final Iterator<T> iterator = getData().iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    public void forEach(Consumer<? super T> action) {
        data.forEach(action);
    }
}
