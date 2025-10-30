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
import com.biit.ks.dto.CategorizationDTO;
import com.biit.ks.persistence.entities.Categorization;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CategorizationConverter extends OpenSearchElementConverter<Categorization, CategorizationDTO, CategorizationConverterRequest> {

    @Override
    protected CategorizationDTO convertElement(CategorizationConverterRequest from) {
        final CategorizationDTO formDTO = new CategorizationDTO();
        BeanUtils.copyProperties(from.getEntity(), formDTO);
        return formDTO;
    }

    @Override
    public Categorization reverse(CategorizationDTO to) {
        if (to == null) {
            return null;
        }
        final Categorization form = new Categorization();
        BeanUtils.copyProperties(to, form);
        return form;
    }
}
