package com.biit.ks.core.controllers;

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

import com.biit.ks.core.converters.CategorizedElementConverter;
import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.core.providers.CategorizedElementProvider;
import com.biit.ks.dto.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;

import java.util.Collection;

public abstract class CategorizedElementController<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        R extends CategorizedElementRepository<E>,
        P extends CategorizedElementProvider<E, R>,
        Rq extends CategorizedElementConverterRequest<E>,
        Cv extends CategorizedElementConverter<E, D, Rq>>
        extends OpenSearchElementController<E, D, R, P, Rq, Cv> {


    protected CategorizedElementController(P provider, Cv converter) {
        super(provider, converter);
    }

    public SearchWrapper<D> searchByCategories(Collection<String> categorizations, QuantifiersOperator quantifiersOperator, Integer from, Integer size) {
        return convertAll(getProvider().searchByCategoryNames(categorizations, quantifiersOperator, from, size));
    }

    public long countByCategories(Collection<String> categorizations, QuantifiersOperator quantifiersOperator) {
        return getProvider().countByCategoryNames(categorizations, quantifiersOperator);
    }
}
