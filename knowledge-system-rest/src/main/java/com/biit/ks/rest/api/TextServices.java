package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.TextController;
import com.biit.ks.core.converters.TextConverter;
import com.biit.ks.core.converters.models.TextConverterRequest;
import com.biit.ks.core.models.TextDTO;
import com.biit.ks.core.providers.TextProvider;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import com.biit.ks.persistence.repositories.TextRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/texts")
public class TextServices extends CategorizedElementServices<Text, TextDTO, TextRepository, TextProvider, TextConverterRequest,
        TextConverter, TextController> {


    protected TextServices(TextController textController) {
        super(textController);
    }



    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a Text.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/basic-auth/download/{uuid}/language/{language}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getText(@PathVariable("uuid") UUID uuid, @PathVariable("language") String language, HttpServletResponse response) {
        final TextDTO text = getController().get(uuid);
        if (text != null) {
            return text.getContent().get(TextLanguages.fromString(language));
        }
        return null;
    }

}
