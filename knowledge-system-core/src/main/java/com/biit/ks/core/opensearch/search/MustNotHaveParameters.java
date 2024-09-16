package com.biit.ks.core.opensearch.search;

import org.springframework.data.util.Pair;

import java.util.List;

public class MustNotHaveParameters extends SearchParameters {
    public MustNotHaveParameters() {
        this(null);
    }
    public MustNotHaveParameters(List<Pair<String, String>> search) {
        super(search);
    }
}
