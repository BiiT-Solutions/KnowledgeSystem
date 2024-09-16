package com.biit.ks.core.opensearch.search;

import org.springframework.data.util.Pair;

import java.util.List;

public class MustHaveParameters extends SearchParameters {
    public MustHaveParameters() {
        this(null);
    }

    public MustHaveParameters(List<Pair<String, String>> search) {
        super(search);
    }
}
