package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FileEntryController;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.Chunk;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.biit.ks.rest.Constants.HEADER.*;
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
  @GetMapping(value = "/{filename:.+}", produces = "video/mp4")
  @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
  @ResponseBody
  public byte[] streamFileName(final HttpServletResponse response,
                           @RequestHeader(value = "Range", required = false) final String range,
                           @PathVariable final String filename
  ) {
    final String[] ranges = range == null ? new String[0] : range.split("-");
    // Skip the first 6 characters "bytes="
    final long skip = range == null? 0 : Long.parseLong(ranges[0].substring(BYTES.length() + 1));
    int size = ranges.length > 1 ? Integer.parseInt(ranges[1]) : maxSize;
    if (size > maxSize) {
      size = maxSize;
    }
    final Chunk chunk = fileEntryController.downloadChunk(filename, skip, size);

    if (chunk == null) {
      throw new FileNotFoundException(this.getClass(), "File not found with path '" + filename + "'.");
    }
    response.setHeader(CONTENT_TYPE, "video/mp4");
    response.setHeader(ACCEPT_RANGES, BYTES);
    response.setHeader(CONTENT_LENGTH, String.valueOf(chunk.getData().length));
    response.setHeader(CONTENT_RANGE, BYTES + " " + skip + "-" + (skip + chunk.getData().length - 1) + "/" + chunk.getFileSize());
    return chunk.getData();
  }
  
}
