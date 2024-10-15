package com.biit.ks.persistence.repositories;

import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Primary
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OpenSearchConfigurator {

    public static final String OPENSEARCH_FILE_INDEX = "test-file-index";

    public static final String OPENSEARCH_CATEGORIZATIONS_INDEX = "test-categorizations";

    public String getOpenSearchFileIndex() {
        return OPENSEARCH_FILE_INDEX;
    }

    public String getOpenSearchCategorizationsIndex() {
        return OPENSEARCH_CATEGORIZATIONS_INDEX;
    }
}
