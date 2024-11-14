package com.biit.ks.persistence.repositories;

import org.springframework.stereotype.Component;

@Component
public class OpenSearchConfigurator implements IOpenSearchConfigurator {

    public static final String OPENSEARCH_FILE_INDEX = "file-index";

    public static final String OPENSEARCH_TEXT_INDEX = "text-index";

    public static final String OPENSEARCH_CATEGORIZATIONS_INDEX = "categorizations";

    @Override
    public String getOpenSearchFileIndex() {
        return OPENSEARCH_FILE_INDEX;
    }

    @Override
    public String getOpenSearchTextIndex() {
        return OPENSEARCH_TEXT_INDEX;
    }

    @Override
    public String getOpenSearchCategorizationsIndex() {
        return OPENSEARCH_CATEGORIZATIONS_INDEX;
    }
}
