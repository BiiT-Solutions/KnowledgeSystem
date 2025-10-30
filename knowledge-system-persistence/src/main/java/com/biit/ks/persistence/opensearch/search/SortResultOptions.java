package com.biit.ks.persistence.opensearch.search;

/*-
 * #%L
 * Knowledge System (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
