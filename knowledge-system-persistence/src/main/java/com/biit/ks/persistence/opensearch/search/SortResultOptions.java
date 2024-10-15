package com.biit.ks.persistence.opensearch.search;

import com.biit.ks.persistence.opensearch.exceptions.OpenSearchInvalidSortingFieldException;
import org.opensearch.client.opensearch._types.SortOptions;
import org.opensearch.client.opensearch._types.SortOrder;

import java.util.List;

public class SortResultOptions {
    public static final List<String> ALLOWED_FIELDS = List.of("createdAt");

    private String field;
    private SortOptionOrder order;

    public SortResultOptions(String field, SortOptionOrder order) {
        setField(field);
        setOrder(order);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        if (!ALLOWED_FIELDS.contains(field)) {
            throw new OpenSearchInvalidSortingFieldException(this.getClass(), "Field '" + field
                    + "' is not allowed by sorting. Allowed fields are '" + ALLOWED_FIELDS + "'.");
        }
        this.field = field;
    }

    public SortOptionOrder getOrder() {
        return order;
    }

    public void setOrder(SortOptionOrder order) {
        this.order = order;
    }

    public SortOptions convert() {
        return new SortOptions.Builder().field(f -> f.field(field).order(order == SortOptionOrder.DESC ? SortOrder.Desc : SortOrder.Asc))
                .build();
    }

}
