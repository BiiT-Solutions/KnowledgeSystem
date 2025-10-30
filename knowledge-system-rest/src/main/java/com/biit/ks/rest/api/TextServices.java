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

import com.biit.ks.core.controllers.TextController;
import com.biit.ks.core.converters.TextConverter;
import com.biit.ks.core.converters.models.TextConverterRequest;
import com.biit.ks.core.providers.TextProvider;
import com.biit.ks.dto.TextDTO;
import com.biit.ks.dto.TextLanguagesDTO;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.TextRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/texts")
public class TextServices extends CategorizedElementServices<Text, TextDTO, TextRepository, TextProvider, TextConverterRequest,
        TextConverter, TextController> {


    protected TextServices(TextController textController) {
        super(textController);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a Text.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/downloads/{uuid}/languages/{language}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getText(@PathVariable("uuid") UUID uuid, @PathVariable("language") String language, HttpServletResponse response) {
        final SearchWrapper<TextDTO> text = getController().get(uuid);
        if (text != null && !text.getData().isEmpty()) {
            return text.getFirst().getContent().get(TextLanguagesDTO.fromString(language));
        }
        return null;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a Text.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/basic-auth/downloads/{uuid}/languages/{language}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getTextBasicAuth(@PathVariable("uuid") UUID uuid, @PathVariable("language") String language, HttpServletResponse response) {
        final SearchWrapper<TextDTO> text = getController().get(uuid);
        if (text != null && !text.getData().isEmpty()) {
            return text.getFirst().getContent().get(TextLanguagesDTO.fromString(language));
        }
        return null;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a Text using its name.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/basic-auth/downloads/name/{name}/languages/{language}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getTextBasicAuth(@PathVariable("name") String name, @PathVariable("language") String language, HttpServletResponse response) {
        final SearchWrapper<TextDTO> text = getController().get(name);
        if (!text.isEmpty()) {
            return text.getFirst().getContent().get(TextLanguagesDTO.fromString(language));
        }
        return null;
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a Text using its name.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/downloads/name/{name}/languages/{language}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getText(@PathVariable("name") String name, @PathVariable("language") String language, HttpServletResponse response) {
        final SearchWrapper<TextDTO> text = getController().get(name);
        if (!text.isEmpty()) {
            return text.getFirst().getContent().get(TextLanguagesDTO.fromString(language));
        }
        return null;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a Text.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/public/downloads/{uuid}/languages/{language}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getPublicText(@PathVariable("uuid") UUID uuid, @PathVariable("language") String language, HttpServletResponse response) {
        final SearchWrapper<TextDTO> text = getController().getPublic(uuid);
        if (text != null && !text.getData().isEmpty()) {
            return text.getFirst().getContent().get(TextLanguagesDTO.fromString(language));
        }
        return null;
    }

}
