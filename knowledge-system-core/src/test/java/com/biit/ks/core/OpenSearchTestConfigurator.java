package com.biit.ks.core;

import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class OpenSearchTestConfigurator implements IOpenSearchConfigurator {

    public static final String OPENSEARCH_FILE_INDEX = "file-index-test";

    public static final String OPENSEARCH_TEXT_INDEX = "text-index-test";

    public static final String OPENSEARCH_CATEGORIZATIONS_INDEX = "categorizations-test";

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
