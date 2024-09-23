package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.FormController;
import com.biit.ks.core.models.FormDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/forms")
public class FormServices {

    private final FormController formController;

    @Autowired
    public FormServices(FormController formController) {
        this.formController = formController;
    }

    @Operation(summary = "Gets a form by name and version")
    @GetMapping(value = "/{name}/version/{version}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FormDTO getByNameAndVersion(@Parameter(description = "Name of the file", required = true) @PathVariable("name") String name,
                                       @Parameter(description = "Version of the file, if you introduce 0 or null value,"
                                               + " the result will be the last version") @PathVariable("version") Integer version) {
        return formController.getByName(name, Objects.requireNonNullElse(version, 0));
    }
}
