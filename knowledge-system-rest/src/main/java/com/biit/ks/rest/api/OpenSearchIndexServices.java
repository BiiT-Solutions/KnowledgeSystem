package com.biit.ks.rest.api;

import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.repositories.IOpenSearchConfigurator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-search/index")
public class OpenSearchIndexServices {

    private final OpenSearchClient openSearchClient;
    private final IOpenSearchConfigurator openSearchConfigurator;

    public OpenSearchIndexServices(OpenSearchClient openSearchClient, IOpenSearchConfigurator openSearchConfigurator) {
        this.openSearchClient = openSearchClient;
        this.openSearchConfigurator = openSearchConfigurator;
    }

    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Deletes an index by name.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {"/{name}"})
    public void delete(@PathVariable("name") String name, Authentication authentication, HttpServletRequest request) {
        openSearchClient.deleteIndex(name);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Creates an index by name.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = {"/{name}"})
    public void create(@PathVariable("name") String name, Authentication authentication, HttpServletRequest request) {
        openSearchClient.createIndex(name);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Deletes all indexes. Do not use this on production!", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {""})
    public void deleteAll(Authentication authentication, HttpServletRequest request) {
        openSearchConfigurator.getAllOpenSearchIndexes().parallelStream().forEach(openSearchClient::deleteIndex);
    }
}
