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

import com.biit.ks.core.controllers.FileEntryController;
import com.biit.ks.core.models.ChunkData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.util.UUID;

import static com.biit.ks.rest.Constants.HEADER.ACCEPT_RANGES;
import static com.biit.ks.rest.Constants.HEADER.CONTENT_LENGTH;
import static com.biit.ks.rest.Constants.HEADER.CONTENT_RANGE;
import static com.biit.ks.rest.Constants.HEADER.CONTENT_TYPE;
import static com.biit.ks.rest.Constants.UNITS.BYTES;

@RestController
@RequestMapping("/stream")
public class StreamServices {
    private final FileEntryController fileEntryController;

    @Value("${seaweed.stream.max-chunk-size}")
    private int maxSize;

    public StreamServices(final FileEntryController fileEntryController) {
        this.fileEntryController = fileEntryController;
    }


    @Operation(summary = "Downloads a file.")
    @GetMapping(value = "/path/**")
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @ResponseBody
    public byte[] streamFileName(final HttpServletResponse response, final HttpServletRequest request,
                                 @RequestHeader(value = "Range", required = false) final String range) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = StringUtils.removeStart(path, "/stream/path");

        return getChunk(path, range, response, false);
    }


    @Operation(summary = "Downloads a file.")
    @GetMapping(value = "/file-entry/uuid/{uuid}")
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @ResponseBody
    public byte[] streamFileEntry(@PathVariable("uuid") UUID uuid,
                                  final HttpServletResponse response, final HttpServletRequest request,
                                  @RequestHeader(value = "Range", required = false) final String range) {
        return getChunk(uuid, range, response, false);
    }


    @Operation(summary = "Downloads a file.")
    @GetMapping(value = "/public/path/**")
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @ResponseBody
    public byte[] streamFileNamePublic(final HttpServletResponse response, final HttpServletRequest request,
                                       @RequestHeader(value = "Range", required = false) final String range) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = StringUtils.removeStart(path, "/stream/public/path");

        return getChunk(path, range, response, true);
    }


    @Operation(summary = "Downloads a file.")
    @GetMapping(value = "/public/file-entry/uuid/{uuid}")
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @ResponseBody
    public byte[] streamFileEntryPublic(@PathVariable("uuid") UUID uuid,
                                        final HttpServletResponse response, final HttpServletRequest request,
                                        @RequestHeader(value = "Range", required = false) final String range) {
        return getChunk(uuid, range, response, true);
    }

    private byte[] getChunk(String path, String range, HttpServletResponse response, boolean checkIfPublic) {
        final long skip = getSkip(range);
        final int size = getSize(range);
        final ChunkData chunk = fileEntryController.downloadChunk(path, skip, size, checkIfPublic);

        response.setHeader(CONTENT_TYPE, chunk.getMimeType());
        response.setHeader(ACCEPT_RANGES, BYTES);
        response.setHeader(CONTENT_LENGTH, String.valueOf(chunk.getData().length));
        response.setHeader(CONTENT_RANGE, BYTES + " " + skip + "-" + (skip + chunk.getData().length - 1) + "/" + chunk.getFileSize());
        return chunk.getData();
    }


    private byte[] getChunk(UUID uuid, String range, HttpServletResponse response, boolean checkIfPublic) {
        final long skip = getSkip(range);
        final int size = getSize(range);
        final ChunkData chunk = fileEntryController.downloadChunk(uuid, skip, size, checkIfPublic);

        response.setHeader(CONTENT_TYPE, chunk.getMimeType());
        response.setHeader(ACCEPT_RANGES, BYTES);
        response.setHeader(CONTENT_LENGTH, String.valueOf(chunk.getData().length));
        response.setHeader(CONTENT_RANGE, BYTES + " " + skip + "-" + (skip + chunk.getData().length - 1) + "/" + chunk.getFileSize());
        return chunk.getData();
    }


    private long getSkip(String range) {
        final String[] ranges = range == null ? new String[0] : range.split("-");
        // Skip the first 6 characters "bytes="
        return range == null ? 0 : Long.parseLong(ranges[0].substring(BYTES.length() + 1));
    }


    private int getSize(String range) {
        final String[] ranges = range == null ? new String[0] : range.split("-");
        int size = ranges.length > 1 ? Integer.parseInt(ranges[1]) : maxSize;
        if (size > maxSize) {
            size = maxSize;
        }
        return size;
    }
}
