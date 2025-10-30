package com.biit.ks.persistence.entities;

/*-
 * #%L
 * Knowledge System (Persistence)
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

public class Form extends CategorizedElement<Long> {
    private static final int MAX_JSON_LENGTH = 100000;


    private Long id;

    private Integer version;

    private String organizationId;

    private String value;


    public Form() {
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

    public String getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
