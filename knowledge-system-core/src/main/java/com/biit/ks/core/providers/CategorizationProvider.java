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

import com.biit.ks.core.exceptions.CategoryAlreadyExistsException;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorizationProvider extends OpenSearchElementProvider<Categorization, CategorizationRepository> {

    private final CategorizationRepository categorizationRepository;

    public CategorizationProvider(CategorizationRepository categorizationRepository) {
        super(categorizationRepository);
        this.categorizationRepository = categorizationRepository;
    }

    //Name is unique on categories
    public SearchWrapper<Categorization> get(String name) {
        return getRepository().get(name);
    }

    public SearchWrapper<Categorization> get(List<String> categorizations) {
        return getRepository().get(categorizations);
    }

    public SearchWrapper<Categorization> create(String categorization, String creatorName) {
        return create(new Categorization(categorization), creatorName);
    }

    public SearchWrapper<Categorization> create(Categorization element, String creatorName) {
        if (element.getCreatedBy() == null && creatorName != null) {
            element.setCreatedBy(creatorName);
        }
        if (!get(element.getName()).isEmpty()) {
            throw new CategoryAlreadyExistsException(this.getClass(), "Already exists a category with name " + element.getName());
        }
        final Categorization stored = save(element);
        KnowledgeSystemLogger.info(this.getClass(), "Entity '{}' created by '{}'.", stored, creatorName);
        return new SearchWrapper<>(stored);
    }

}
