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

import com.biit.ks.core.providers.ThumbnailProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/thumbnails")
public class ThumbnailServices {
    private final ThumbnailProvider thumbnailProvider;


    public ThumbnailServices(final ThumbnailProvider thumbnailProvider) {
        this.thumbnailProvider = thumbnailProvider;
    }


    @Operation(summary = "Downloads a thumbnail.")
    @GetMapping(value = "/public/downloads/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] download(@PathVariable UUID uuid, HttpServletResponse response) {
        return thumbnailProvider.getThumbnail(uuid);
    }

}
