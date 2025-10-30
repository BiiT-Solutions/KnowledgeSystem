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

import com.biit.ks.core.controllers.CategorizedElementController;
import com.biit.ks.core.converters.CategorizedElementConverter;
import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.core.providers.CategorizedElementProvider;
import com.biit.ks.dto.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Collection;

public abstract class CategorizedElementServices<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        R extends CategorizedElementRepository<E>,
        P extends CategorizedElementProvider<E, R>,
        Rq extends CategorizedElementConverterRequest<E>,
        Cv extends CategorizedElementConverter<E, D, Rq>,
        C extends CategorizedElementController<E, D, R, P, Rq, Cv>>
        extends OpenSearchElementServices<E, D, R, P, Rq, Cv, C> {

    protected CategorizedElementServices(C controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets elements by categories.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<D> downloadByCategories(@RequestParam(name = "category", required = true) String[] category,
                                              @RequestParam(name = "quantifier", required = false) QuantifiersOperator quantifiersOperator,
                                              @RequestParam(name = "from", required = false) Integer from,
                                              @RequestParam(name = "size", required = false) Integer size,
                                              Authentication authentication, HttpServletResponse response) {
        return getResponse(getController().searchByCategories(Arrays.asList(category), quantifiersOperator, from, size), response);
    }
}
