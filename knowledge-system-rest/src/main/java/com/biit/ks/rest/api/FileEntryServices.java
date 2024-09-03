package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FileEntryController;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.FileEntryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileEntryServices {
    private final FileEntryController fileEntryController;


    protected FileEntryServices(FileEntryController fileEntryController) {
        this.fileEntryController = fileEntryController;
    }


    @PreAuthorize("hasAnyRole('ROLE_EDITOR', 'ROLE_ADMIN')")
    @Operation(summary = "Uploads a file.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public FileEntryDTO upload(@RequestParam("file") MultipartFile file,
                               @RequestBody FileEntryDTO fileEntryDTO,
                               Authentication authentication, HttpServletRequest request) {
        return fileEntryController.upload(file, fileEntryDTO, authentication.getName());
    }


    @PreAuthorize("hasAnyRole('ROLE_VIEWER', 'ROLE_EDITOR', 'ROLE_ADMIN')")
    @Operation(summary = "Downloads a file.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/download/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable("uuid") UUID uuid,
                                             Authentication authentication, HttpServletResponse response) {
        final Resource file = fileEntryController.downloadAsResource(uuid);

        if (file == null) {
            throw new FileNotFoundException(this.getClass(), "File not found with uuid '" + uuid + "'.");
        }

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("file").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return ResponseEntity.ok().body(file);
    }


    @PreAuthorize("hasAnyRole('ROLE_VIEWER', 'ROLE_EDITOR', 'ROLE_ADMIN')")
    @Operation(summary = "Downloads a file.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/download/{filename:.+}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String filename, HttpServletResponse response) {
        final Resource file = fileEntryController.downloadAsResource(filename);

        if (file == null) {
            throw new FileNotFoundException(this.getClass(), "File not found with path '" + filename + "'.");
        }

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("file").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return ResponseEntity.ok().body(file);
    }
}
