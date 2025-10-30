package com.biit.ks.core.providers;

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

import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;

import java.util.Collection;

public abstract class CategorizedElementProvider<E extends CategorizedElement<?>, R extends CategorizedElementRepository<E>>
        extends OpenSearchElementProvider<E, R> {

    private final CategorizedElementRepository<E> categorizedElementRepository;

    protected CategorizedElementProvider(R categorizedElementRepository) {
        super(categorizedElementRepository);
        this.categorizedElementRepository = categorizedElementRepository;
    }

    public SearchWrapper<E> searchByCategory(Categorization categorization, Integer from, Integer size) {
        return categorizedElementRepository.searchByCategory(categorization, from, size);
    }

    public SearchWrapper<E> searchByCategory(String categorizationName, Integer from, Integer size) {
        return categorizedElementRepository.searchByCategory(categorizationName, from, size);
    }

    public SearchWrapper<E> searchByCategories(Collection<Categorization> categorizations, QuantifiersOperator quantifiersOperator,
                                               Integer from, Integer size) {
        return categorizedElementRepository.searchByCategories(categorizations, quantifiersOperator, from, size);
    }

    public SearchWrapper<E> searchByCategoryNames(Collection<String> categorizations, QuantifiersOperator quantifiersOperator, Integer from, Integer size) {
        return categorizedElementRepository.searchByCategoryNames(categorizations, quantifiersOperator, from, size);
    }

    public long countByCategoryNames(Collection<String> categorizations, QuantifiersOperator quantifiersOperator) {
        return categorizedElementRepository.countByCategoryNames(categorizations, quantifiersOperator);
    }
}
