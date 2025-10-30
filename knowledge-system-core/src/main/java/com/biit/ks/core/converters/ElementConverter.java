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

import com.biit.ks.persistence.entities.Element;
import com.biit.server.controller.converters.SimpleConverter;
import com.biit.server.controllers.models.CreatedElementDTO;
import com.biit.server.converters.models.ConverterRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ElementConverter<ENTITY extends Element<?>, DTO extends CreatedElementDTO, REQUEST extends ConverterRequest<ENTITY>>
        extends SimpleConverter<ENTITY, DTO, REQUEST> {
    public ElementConverter() {
    }

    protected abstract DTO convertElement(REQUEST from);

    public DTO convert(REQUEST from) {
        return from != null && from.hasEntity() ? this.convertElement(from) : null;
    }

    @Override
    public List<DTO> convertAll(Collection<REQUEST> from) {
        if (from == null) {
            return new ArrayList<>();
        }
        return from.stream().map(this::convert).sorted(Comparator.comparing(CreatedElementDTO::getCreatedAt,
                Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    @Override
    public List<ENTITY> reverseAll(Collection<DTO> to) {
        if (to == null) {
            return new ArrayList<>();
        }
        return to.stream().map(this::reverse).collect(Collectors.toList());
    }
}
