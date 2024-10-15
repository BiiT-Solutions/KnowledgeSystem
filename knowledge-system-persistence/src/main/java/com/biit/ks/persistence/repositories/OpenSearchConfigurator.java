package com.biit.ks.persistence.repositories;

import org.springframework.stereotype.Component;

@Component
public class OpenSearchConfigurator {

    public static final String OPENSEARCH_FILE_INDEX = "file-index";

    public static final String OPENSEARCH_CATEGORIZATIONS_INDEX = "categorizations";

    public String getOpenSearchFileIndex() {
        return OPENSEARCH_FILE_INDEX;
    }

    public String getOpenSearchCategorizationsIndex() {
        return OPENSEARCH_CATEGORIZATIONS_INDEX;
    }
}
