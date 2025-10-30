package com.biit.ks.core.models;

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

import com.biit.server.controllers.models.ElementDTO;

public class FormRulesLinkDTO extends ElementDTO<Long> {

    private Long id;

    private Long formId;
    private Long rulesId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFormId() {
        return this.formId;
    }

    public Long getRulesId() {
        return this.rulesId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }

    public void setRulesId(Long rulesId) {
        this.rulesId = rulesId;
    }
}
