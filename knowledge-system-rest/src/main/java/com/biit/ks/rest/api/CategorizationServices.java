package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.CategorizationController;
import com.biit.ks.core.converters.CategorizationConverter;
import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.dto.CategorizationDTO;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorizations")
public class CategorizationServices extends OpenSearchElementServices<Categorization, CategorizationDTO, CategorizationRepository,
        CategorizationProvider, CategorizationConverterRequest, CategorizationConverter, CategorizationController> {


    protected CategorizationServices(CategorizationController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Creates a category.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/{categorization}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CategorizationDTO create(@PathVariable("categorization") String categorization, Authentication authentication, HttpServletResponse response) {
        return getController().create(categorization, authentication.getName());
    }


}
