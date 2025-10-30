package com.biit.ks.dto;

/*-
 * #%L
 * Knowledge System (DTO)
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

import java.io.Serial;
import java.util.List;

public abstract class CategorizedElementDTO<U> extends OpenSearchElementDTO<U> {

    @Serial
    private static final long serialVersionUID = 7445445590960082105L;

    private List<CategorizationDTO> categorizations;

    public List<CategorizationDTO> getCategorizations() {
        return categorizations;
    }

    public void setCategorizations(List<CategorizationDTO> categorizations) {
        this.categorizations = categorizations;
    }
}
