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

import com.biit.ks.core.converters.CategorizationConverter;
import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.core.providers.CategorizedElementProvider;
import com.biit.ks.dto.CategorizationDTO;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class CategorizationController extends OpenSearchElementController<Categorization, CategorizationDTO, CategorizationRepository,
        CategorizationProvider, CategorizationConverterRequest, CategorizationConverter> {
    private static final int SIZE = 10;

    private final CategorizationProvider categorizationProvider;

    private final List<CategorizedElementProvider<? extends CategorizedElement<?>, ? extends CategorizedElementRepository<?>>> providersWithCategories;

    public CategorizationController(CategorizationProvider categorizationProvider, CategorizationConverter categorizationConverter,
                                    List<CategorizedElementProvider<? extends CategorizedElement<?>, ? extends CategorizedElementRepository<?>>>
                                            providersWithCategories) {
        super(categorizationProvider, categorizationConverter);
        this.categorizationProvider = categorizationProvider;
        this.providersWithCategories = providersWithCategories;
    }


    @Override
    protected CategorizationConverterRequest createConverterRequest(Categorization categorization) {
        return new CategorizationConverterRequest(categorization);
    }


    public CategorizationDTO create(String categorization, String creatorName) {
        return create(new CategorizationDTO(categorization), creatorName);
    }


    @Override
    public CategorizationDTO create(CategorizationDTO dto, String creatorName) {
        validate(dto);
        return convert(getProvider().create(reverse(dto), creatorName).getFirst());
    }


    @Override
    public Collection<CategorizationDTO> create(Collection<CategorizationDTO> categorizationDTOS, String creatorName) {
        final List<CategorizationDTO> results = new ArrayList<>();
        categorizationDTOS.forEach(fileEntryDTO -> create(fileEntryDTO, creatorName));
        return results;
    }

    public SearchWrapper<CategorizationDTO> get(String categorizationName) {
        final SearchWrapper<Categorization> categorization = getProvider().get(categorizationName);

        return convertAll(categorization);
    }


    @Scheduled(cron = "@midnight")
    public void deleteOrphanCategories() {
        int loop = 0;
        SearchWrapper<Categorization> categorizations = categorizationProvider.getAll(0, SIZE);
        while (!categorizations.getData().isEmpty()) {
            for (Categorization categorization : categorizations.getData()) {
                //Check if a category is not used.
                boolean used = false;
                for (CategorizedElementProvider<? extends CategorizedElement<?>, ? extends CategorizedElementRepository<?>> provider
                        : providersWithCategories) {
                    if (!provider.searchByCategory(categorization, 0, 1).getData().isEmpty()) {
                        used = true;
                        break;
                    }
                }
                if (used) {
                    continue;
                }
                //Remove it.
                categorizationProvider.delete(categorization);
            }
            loop++;
            categorizations = categorizationProvider.getAll(loop * SIZE, SIZE);
        }
    }
}
