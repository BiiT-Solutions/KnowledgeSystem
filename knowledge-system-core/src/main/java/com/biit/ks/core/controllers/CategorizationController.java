package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.CategorizationConverter;
import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.CategorizationDTO;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.ks.persistence.repositories.CategorizationRepository;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class CategorizationController extends OpenSearchElementController<Categorization, CategorizationDTO, CategorizationRepository,
        CategorizationProvider, CategorizationConverterRequest, CategorizationConverter> {


    public CategorizationController(CategorizationProvider categorizationProvider, CategorizationConverter categorizationConverter) {
        super(categorizationProvider, categorizationConverter);
    }


    @Override
    protected CategorizationConverterRequest createConverterRequest(Categorization categorization) {
        return new CategorizationConverterRequest(categorization);
    }


    public CategorizationDTO create(String categorization, String creatorName) {
        return create(new CategorizationDTO(categorization), creatorName);
    }


    @Override
    public CategorizationDTO create(CategorizationDTO dto, String creatorName) {
        validate(dto);
        return convert(getProvider().create(reverse(dto), creatorName));
    }


    @Override
    public Collection<CategorizationDTO> create(Collection<CategorizationDTO> categorizationDTOS, String creatorName) {
        final List<CategorizationDTO> results = new ArrayList<>();
        categorizationDTOS.forEach(fileEntryDTO -> create(fileEntryDTO, creatorName));
        return results;
    }

    public CategorizationDTO get(String categorization) {
        final Categorization fileEntry =
                getProvider().get(categorization).orElseThrow(() -> new FileNotFoundException(this.getClass(),
                        "No category with name '" + categorization + "'."));

        return convert(fileEntry);
    }
}
