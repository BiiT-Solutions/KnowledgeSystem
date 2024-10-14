package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.OpenSearchElementController;
import com.biit.ks.core.converters.OpenSearchElementConverter;
import com.biit.ks.core.converters.models.OpenSearchElementConverterRequest;
import com.biit.ks.core.models.OpenSearchElementDTO;
import com.biit.ks.core.providers.OpenSearchElementProvider;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;
import com.biit.server.rest.SimpleServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

public abstract class OpenSearchElementService<
        E extends OpenSearchElement<?>,
        D extends OpenSearchElementDTO<?>,
        R extends OpenSearchElementRepository<E>,
        P extends OpenSearchElementProvider<E, R>,
        Rq extends OpenSearchElementConverterRequest<E>,
        Cv extends OpenSearchElementConverter<E, D, Rq>,
        C extends OpenSearchElementController<E, D, R, P, Rq, Cv>>
        extends SimpleServices<E, D, P, Rq, Cv, C> {

    protected OpenSearchElementService(C controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets an entity.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(value = {"/{uuid}"}, produces = {"application/json"})
    public void get(@PathVariable("uuid") UUID uuid, Authentication authentication, HttpServletRequest request) {
        this.getController().get(uuid);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Deletes an entity.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = {"/delete"}, produces = {"application/json"})
    public void delete(@RequestBody D dto, Authentication authentication, HttpServletRequest request) {
        this.getController().delete(dto, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets an entity.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {"/{uuid}"}, produces = {"application/json"})
    public void delete(@PathVariable("uuid") UUID uuid, Authentication authentication, HttpServletRequest request) {
        this.getController().delete(uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Search for an element.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/search/{query:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<D> search(@PathVariable String query,
                          @RequestParam(name = "from", required = false) Integer from,
                          @RequestParam(name = "size", required = false) Integer size,
                          HttpServletResponse response) {
        return getController().search(query.replace("query:", ""), from, size);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all elements.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<D> getAll(
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size,
            Authentication authentication, HttpServletResponse response) {
        return getController().getAll(from, size);
    }
}
