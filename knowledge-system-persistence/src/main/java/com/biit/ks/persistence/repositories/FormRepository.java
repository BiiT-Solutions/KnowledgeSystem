package com.biit.ks.persistence.repositories;

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

import com.biit.ks.persistence.entities.Form;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class FormRepository {
    public Optional<Form> findById(Long id) {
        return Optional.empty();
    }

    public List<Form> findAll() {
        return null;
    }

    public Collection<Form> findByNameOrderByVersionDesc(String name) {
        return null;
    }

    public Optional<Form> findByNameAndVersion(String name, Integer version) {
        return Optional.empty();
    }

    public Form save(Form form) {
        return null;
    }
}
