package com.biit.ks.rest.api;

import com.biit.ks.core.seaweed.SeaweedClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seaweed")
public class SeaweedServices {

    private final SeaweedClient seaweedClient;

    public SeaweedServices(SeaweedClient seaweedClient) {
        this.seaweedClient = seaweedClient;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.adminPrivilege)")
    @Operation(summary = "Deletes all entries. Do not use this on production!", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = {""})
    public void deleteAll(Authentication authentication, HttpServletRequest request) {
        seaweedClient.wipeOut();
    }
}
