package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FileEntryController;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.ChunkData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

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
  @GetMapping(value = "/**")
  @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
  @ResponseBody
  public byte[] streamFileName(final HttpServletResponse response, final HttpServletRequest request,
                           @RequestHeader(value = "Range", required = false) final String range) {
    String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    path = StringUtils.removeStart(path, "/stream");

    final String[] ranges = range == null ? new String[0] : range.split("-");
    // Skip the first 6 characters "bytes="
    final long skip = range == null ? 0 : Long.parseLong(ranges[0].substring(BYTES.length() + 1));
    int size = ranges.length > 1 ? Integer.parseInt(ranges[1]) : maxSize;
    if (size > maxSize) {
      size = maxSize;
    }
    final ChunkData chunk = fileEntryController.downloadChunk(path, skip, size);

    if (chunk == null) {
      throw new FileNotFoundException(this.getClass(), "File not found with path '" + path + "'.");
    }

    response.setHeader(CONTENT_TYPE, chunk.getMimeType());
    response.setHeader(ACCEPT_RANGES, BYTES);
    response.setHeader(CONTENT_LENGTH, String.valueOf(chunk.getData().length));
    response.setHeader(CONTENT_RANGE, BYTES + " " + skip + "-" + (skip + chunk.getData().length - 1) + "/" + chunk.getFileSize());
    return chunk.getData();
  }
}
