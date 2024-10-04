package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FileEntryController;
import com.biit.ks.core.converters.FileEntryConverter;
import com.biit.ks.core.converters.models.FileEntryConverterRequest;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.FileEntryDTO;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.repositories.FileEntryRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileEntryServices extends CategorizedElementServices<FileEntry, FileEntryDTO, FileEntryRepository, FileEntryProvider, FileEntryConverterRequest,
        FileEntryConverter, FileEntryController> {


    protected FileEntryServices(FileEntryController fileEntryController) {
        super(fileEntryController);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Uploads a file.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileEntryDTO upload(@RequestParam("file") MultipartFile file,
                               @RequestPart(required = false) FileEntryDTO fileEntryDTO,
                               @RequestParam(name = "force", required = false) Optional<Boolean> forceRewrite,
                               Authentication authentication, HttpServletRequest request) {
        return getController().upload(file, fileEntryDTO, forceRewrite.isPresent() && forceRewrite.get(),
                authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Downloads a file metadata.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/uuid/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FileEntryDTO downloadMetadata(@PathVariable("uuid") UUID uuid, Authentication authentication, HttpServletResponse response) {
        return getController().getMetadata(uuid);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Downloads a file.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/download/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable("uuid") UUID uuid, Authentication authentication, HttpServletResponse response) {
        final Resource file = getController().downloadAsResource(uuid, false, authentication.getName());

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("file").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return ResponseEntity.ok().body(file);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Downloads a file.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/public/download/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadPublic(@PathVariable("uuid") UUID uuid, HttpServletResponse response) {
        final Resource file = getController().downloadAsResource(uuid, true, "public");

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("file").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return ResponseEntity.ok().body(file);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Downloads a file.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/download/{filename:.+}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String filename, HttpServletResponse response) {
        final Resource file = getController().downloadAsResource(filename);

        if (file == null) {
            throw new FileNotFoundException(this.getClass(), "File not found with path '" + filename + "'.");
        }

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("file").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return ResponseEntity.ok().body(file);
    }


}
