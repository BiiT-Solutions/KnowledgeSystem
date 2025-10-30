package com.biit.ks.core.converters;

/*-
 * #%L
 * Knowledge System (Core)
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

import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.dto.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;

import java.util.ArrayList;

public abstract class CategorizedElementConverter<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        Rq extends CategorizedElementConverterRequest<E>>
        extends OpenSearchElementConverter<E, D, Rq> {

    private final CategorizationConverter categorizationConverter;

    protected CategorizedElementConverter(CategorizationConverter categorizationConverter) {
        this.categorizationConverter = categorizationConverter;
    }

    public void copyCategorizations(E from, D to) {
        to.setCategorizations(new ArrayList<>());
        if (from.getCategorizations() != null) {
            from.getCategorizations().forEach(categorization -> to.getCategorizations().add(
                    categorizationConverter.convert(new CategorizationConverterRequest(categorization))));
        }
    }


    public void copyCategorizations(D from, E to) {
        to.setCategorizations(new ArrayList<>());
        if (from.getCategorizations() != null) {
            from.getCategorizations().forEach(categorizationDTO -> to.getCategorizations().add(
                    categorizationConverter.reverse(categorizationDTO)));
        }
    }
}
