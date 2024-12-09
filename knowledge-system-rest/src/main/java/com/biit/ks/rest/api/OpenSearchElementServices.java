package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.OpenSearchElementController;
import com.biit.ks.core.converters.OpenSearchElementConverter;
import com.biit.ks.core.converters.models.OpenSearchElementConverterRequest;
import com.biit.ks.core.providers.OpenSearchElementProvider;
import com.biit.ks.dto.OpenSearchElementDTO;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;
import com.biit.server.rest.SimpleServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
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

public abstract class OpenSearchElementServices<
        E extends OpenSearchElement<?>,
        D extends OpenSearchElementDTO<?>,
        R extends OpenSearchElementRepository<E>,
        P extends OpenSearchElementProvider<E, R>,
        Rq extends OpenSearchElementConverterRequest<E>,
        Cv extends OpenSearchElementConverter<E, D, Rq>,
        C extends OpenSearchElementController<E, D, R, P, Rq, Cv>>
        extends SimpleServices<E, D, P, Rq, Cv, C> {

    public static final String TOTAL_ELEMENT_HEADER = "X-Total-Elements";

    @Value("${include.total.elements.header:false}")
    private boolean addTotalElementHeader;

    protected OpenSearchElementServices(C controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets an entity.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(value = {"/{uuid}"}, produces = {"application/json"})
    public D get(@PathVariable("uuid") UUID uuid, Authentication authentication, HttpServletRequest request) {
        return this.getController().get(uuid);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Deletes an entity.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = {"/delete"}, produces = {"application/json"})
    public void delete(@RequestBody D dto, Authentication authentication, HttpServletRequest request) {
        this.getController().delete(dto, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Deletes an entity by uuid.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {"/{uuid}"}, produces = {"application/json"})
    public void delete(@PathVariable("uuid") UUID uuid, Authentication authentication, HttpServletRequest request) {
        this.getController().delete(uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Search for elements.", description = "Any text that must be present on the main attributes to search.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/search/{value:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<D> search(@PathVariable String value,
                          @RequestParam(name = "from", required = false) Integer from,
                          @RequestParam(name = "size", required = false) Integer size,
                          @RequestParam(name = "includeTotalElementsHeader", required = false, defaultValue = "false") boolean includeTotalElementsHeader,
                          HttpServletResponse response) {
        if (addTotalElementHeader || includeTotalElementsHeader) {
            response.addHeader(TOTAL_ELEMENT_HEADER, String.valueOf(getController().count(value.replace("value:", ""))));
        }
        return getController().search(value.replace("value:", ""), from, size);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Search for a elements using a simple structure.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<D> search(@RequestBody SimpleSearch query,
                          @RequestParam(name = "from", required = false) Integer from,
                          @RequestParam(name = "size", required = false) Integer size,
                          @RequestParam(name = "includeTotalElementsHeader", required = false, defaultValue = "false") boolean includeTotalElementsHeader,
                          HttpServletResponse response) {
        if (addTotalElementHeader || includeTotalElementsHeader) {
            response.addHeader(TOTAL_ELEMENT_HEADER, String.valueOf(getController().count(query)));
        }
        return getController().search(query, from, size);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all elements.", description = "If settings 'includeTotalElementsHeader' parameter, performs and extra search to count "
            + "the total number of elements.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<D> getAll(
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "includeTotalElementsHeader", required = false, defaultValue = "false") boolean includeTotalElementsHeader,
            Authentication authentication, HttpServletResponse response) {
        if (addTotalElementHeader || includeTotalElementsHeader) {
            response.addHeader(TOTAL_ELEMENT_HEADER, String.valueOf(getController().count()));
        }
        return getController().getAll(from, size);
    }
}
