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

import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-search/index")
public class OpenSearchIndexServices {

    private final OpenSearchClient openSearchClient;
    private final IOpenSearchConfigurator openSearchConfigurator;

    public OpenSearchIndexServices(OpenSearchClient openSearchClient, IOpenSearchConfigurator openSearchConfigurator) {
        this.openSearchClient = openSearchClient;
        this.openSearchConfigurator = openSearchConfigurator;
    }

    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Deletes an index by name.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {"/{name}"})
    public void delete(@PathVariable("name") String name, Authentication authentication, HttpServletRequest request) {
        openSearchClient.deleteIndex(name);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Creates an index by name.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = {"/{name}"})
    public void create(@PathVariable("name") String name, Authentication authentication, HttpServletRequest request) {
        openSearchClient.createIndex(name);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Deletes all indexes. Do not use this on production!", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {""})
    public void deleteAll(Authentication authentication, HttpServletRequest request) {
        openSearchConfigurator.getAllOpenSearchIndexes().parallelStream().forEach(openSearchClient::deleteIndex);
    }
}
