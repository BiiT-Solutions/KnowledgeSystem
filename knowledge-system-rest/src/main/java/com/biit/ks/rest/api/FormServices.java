package com.biit.ks.rest.api;

/*-
 * #%L
 * Knowledge System (Rest)
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

import com.biit.ks.core.controllers.FormController;
import com.biit.ks.core.models.FormDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/forms")
public class FormServices {

    private final FormController formController;

    @Autowired
    public FormServices(FormController formController) {
        this.formController = formController;
    }

    @Operation(summary = "Gets a form by name and version")
    @GetMapping(value = "/{name}/version/{version}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FormDTO getByNameAndVersion(@Parameter(description = "Name of the file", required = true) @PathVariable("name") String name,
                                       @Parameter(description = "Version of the file, if you introduce 0 or null value,"
                                               + " the result will be the last version") @PathVariable("version") Integer version) {
        return formController.getByName(name, Objects.requireNonNullElse(version, 0));
    }
}
