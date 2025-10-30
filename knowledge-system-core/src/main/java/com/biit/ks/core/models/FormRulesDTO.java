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

public class FormRulesDTO extends ElementDTO<Long> {

    private Long id;

    private String name;

    private Integer version;

    private String organizationId;

    private String rules;

    private String metadata;

    public FormRulesDTO() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() {
        return this.organizationId;
    }

    public String getRules() {
        return this.rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
