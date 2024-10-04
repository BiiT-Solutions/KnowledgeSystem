package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.CategorizationController;
import com.biit.ks.core.converters.CategorizationConverter;
import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.models.CategorizationDTO;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.server.rest.SimpleServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorizations")
public class CategorizationServices extends SimpleServices<Categorization, CategorizationDTO, CategorizationProvider, CategorizationConverterRequest,
        CategorizationConverter, CategorizationController> {


    protected CategorizationServices(CategorizationController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Creates a category.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/{categorization}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CategorizationDTO create(@PathVariable("categorization") String categorization, Authentication authentication, HttpServletResponse response) {
        return getController().create(categorization, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all categories.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<CategorizationDTO> getAll(
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size,
            Authentication authentication, HttpServletResponse response) {
        return getController().getAll(from, size);
    }
}
