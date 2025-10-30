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

import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;

import java.util.UUID;

public class OpenSearchElementProvider<E extends OpenSearchElement<?>, R extends OpenSearchElementRepository<E>> {

    private final R openSearchElementRepository;


    public OpenSearchElementProvider(R openSearchElementRepository) {
        this.openSearchElementRepository = openSearchElementRepository;
    }


    public SearchWrapper<E> search(SearchPredicates searchPredicates) {
        return getRepository().search(searchPredicates);
    }


    public SearchWrapper<E> search(SimpleSearch searchQuery, Integer from, Integer size) {
        return getRepository().search(searchQuery, from, size);
    }


    public R getRepository() {
        return openSearchElementRepository;
    }

    public E save(E element) {
        return getRepository().save(element);
    }

    public E update(E element) {
        return getRepository().update(element);
    }


    public void delete(E element) {
        getRepository().delete(element);
    }


    public void delete(UUID id) {
        getRepository().delete(id);
    }


    public SearchWrapper<E> search(String value, Integer from, Integer size) {
        return getRepository().search(value, from, size);
    }

    public long count() {
        return getRepository().count();
    }

    public long count(String value) {
        return getRepository().count(value);
    }

    public long count(SimpleSearch simpleSearch) {
        return getRepository().count(simpleSearch);
    }


    public SearchWrapper<E> get(UUID uuid) {
        if (uuid == null) {
            return new SearchWrapper<>();
        }
        return new SearchWrapper<>(getRepository().get(uuid)
                .orElseThrow(() -> new FileNotFoundException(this.getClass(), "No element with uuid '" + uuid + "'.")));
    }

    public SearchWrapper<E> getAll(Integer from, Integer size) {
        return getRepository().getAll(from, size);
    }
}
