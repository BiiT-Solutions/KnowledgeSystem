package com.biit.ks.rest.api;

import com.biit.ks.core.controllers.CategorizedElementController;
import com.biit.ks.core.converters.CategorizedElementConverter;
import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.core.providers.CategorizedElementProvider;
import com.biit.ks.dto.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

public abstract class CategorizedElementServices<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        R extends CategorizedElementRepository<E>,
        P extends CategorizedElementProvider<E, R>,
        Rq extends CategorizedElementConverterRequest<E>,
        Cv extends CategorizedElementConverter<E, D, Rq>,
        C extends CategorizedElementController<E, D, R, P, Rq, Cv>>
        extends OpenSearchElementServices<E, D, R, P, Rq, Cv, C> {

    @Value("${include.total.elements.header:false}")
    private boolean addTotalElementHeader;

    protected CategorizedElementServices(C controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets elements by categories.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<D> downloadByCategories(@RequestParam(name = "category", required = true) String[] category,
                                        @RequestParam(name = "quantifier", required = false) QuantifiersOperator quantifiersOperator,
                                        @RequestParam(name = "from", required = false) Integer from,
                                        @RequestParam(name = "size", required = false) Integer size,
                                        @RequestParam(name = "includeTotalElementsHeader", required = false, defaultValue = "false")
                                        boolean includeTotalElementsHeader,
                                        Authentication authentication, HttpServletResponse response) {
        if (addTotalElementHeader || includeTotalElementsHeader) {
            response.addHeader(TOTAL_ELEMENT_HEADER, String.valueOf(getController().countByCategories(Arrays.asList(category), quantifiersOperator)));
        }
        return getController().searchByCategories(Arrays.asList(category), quantifiersOperator, from, size);
    }
}
