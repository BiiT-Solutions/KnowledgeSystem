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

import com.biit.ks.core.controllers.CategorizationController;
import com.biit.ks.core.converters.CategorizationConverter;
import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.dto.CategorizationDTO;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorizations")
public class CategorizationServices extends OpenSearchElementServices<Categorization, CategorizationDTO, CategorizationRepository,
        CategorizationProvider, CategorizationConverterRequest, CategorizationConverter, CategorizationController> {


    protected CategorizationServices(CategorizationController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Creates a category.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/name/{categorization}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CategorizationDTO get(@PathVariable("categorization") String categorization, Authentication authentication, HttpServletResponse response) {
        return getResponseFirstData(getController().get(categorization), response);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Creates a category.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/name/{categorization}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CategorizationDTO create(@PathVariable("categorization") String categorization, Authentication authentication, HttpServletResponse response) {
        return getController().create(categorization, authentication.getName());
    }


}
