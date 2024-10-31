package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.ThumbnailController;
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
    private final ThumbnailController thumbnailController;


    public ThumbnailServices(final ThumbnailController thumbnailController) {
        this.thumbnailController = thumbnailController;
    }


    @Operation(summary = "Downloads a thumbnail.")
    @GetMapping(value = "/public/download/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] download(@PathVariable UUID uuid, HttpServletResponse response) {
        return thumbnailController.getThumbnail(uuid);
    }

}
