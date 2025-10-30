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

import com.biit.ks.core.seaweed.SeaweedClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seaweed")
public class SeaweedServices {

    private final SeaweedClient seaweedClient;

    public SeaweedServices(SeaweedClient seaweedClient) {
        this.seaweedClient = seaweedClient;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Deletes all entries. Do not use this on production!", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {""})
    public void deleteAll(Authentication authentication, HttpServletRequest request) {
        seaweedClient.wipeOut();
    }
}
