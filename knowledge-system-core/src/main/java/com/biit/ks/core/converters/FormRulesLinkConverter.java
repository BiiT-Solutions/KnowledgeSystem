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

import com.biit.ks.core.converters.models.FormRulesLinkConverterRequest;
import com.biit.ks.core.models.FormRulesLinkDTO;
import com.biit.ks.persistence.entities.FormRulesLink;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class FormRulesLinkConverter extends ElementConverter<FormRulesLink, FormRulesLinkDTO, FormRulesLinkConverterRequest> {

    @Override
    protected FormRulesLinkDTO convertElement(FormRulesLinkConverterRequest from) {
        final FormRulesLinkDTO applicationDTO = new FormRulesLinkDTO();
        BeanUtils.copyProperties(from.getEntity(), applicationDTO);
        return applicationDTO;
    }

    @Override
    public FormRulesLink reverse(FormRulesLinkDTO to) {
        if (to == null) {
            return null;
        }
        final FormRulesLink application = new FormRulesLink();
        BeanUtils.copyProperties(to, application);
        return application;
    }
}
