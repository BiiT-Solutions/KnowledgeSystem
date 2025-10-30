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

import com.biit.ks.core.converters.models.OpenSearchElementConverterRequest;
import com.biit.ks.dto.OpenSearchElementDTO;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.server.controller.converters.SimpleConverter;

public abstract class OpenSearchElementConverter<
        E extends OpenSearchElement<?>,
        D extends OpenSearchElementDTO<?>,
        Rq extends OpenSearchElementConverterRequest<E>>
        extends SimpleConverter<E, D, Rq> {
}
