package com.biit.ks.persistence.opensearch.search;

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
