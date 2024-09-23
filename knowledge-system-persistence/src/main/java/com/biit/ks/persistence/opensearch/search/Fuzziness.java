package com.biit.ks.persistence.opensearch.search;

public enum Fuzziness {

    AUTO,

    FIELD,

    ONE,

    TWO,

    X_FIELD_NAME,

    ZERO;

    public String tag() {
        return name().toLowerCase();
    }
}
