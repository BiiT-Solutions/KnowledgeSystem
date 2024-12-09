package com.biit.ks.persistence.opensearch.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResponseWrapper<T> {
    private Collection<T> data;
    private long count = 0;

    public ResponseWrapper() {
        this(new ArrayList<>());
    }

    public ResponseWrapper(Collection<T> data) {
        this.data = data;
        this.count = data.size();
    }

    public ResponseWrapper(T data) {
        this();
        this.data.add(data);
    }

    public Collection<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
