package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.CategorizationConverter;
import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.exceptions.CategoryAlreadyExistsException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.CategorizationDTO;
import com.biit.ks.core.providers.CategorizationProvider;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.server.controller.SimpleController;
import com.biit.server.logger.DtoControllerLogger;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Controller
public class CategorizationController extends SimpleController<Categorization, CategorizationDTO, CategorizationProvider,
        CategorizationConverterRequest, CategorizationConverter> {


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
        if (dto.getCreatedBy() == null && creatorName != null) {
            dto.setCreatedBy(creatorName);
        }
        if (get(dto.getName()) != null) {
            throw new CategoryAlreadyExistsException(this.getClass(), "Already exists a category with name " + dto.getName());
        }
        validate(dto);
        final CategorizationDTO dtoStored = convert(getProvider().save(getConverter().reverse(dto)));
        DtoControllerLogger.info(this.getClass(), "Entity '{}' created by '{}'.", dtoStored, creatorName);
        return dtoStored;
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


    public CategorizationDTO get(UUID uuid) {
        final Categorization fileEntry =
                getProvider().get(uuid).orElseThrow(() -> new FileNotFoundException(this.getClass(), "No category with uuid '" + uuid + "'."));

        return convert(fileEntry);
    }


    public List<CategorizationDTO> getAll(Integer from, Integer size) {
        final List<Categorization> results = getProvider().getAll(from, size);
        return convertAll(results);
    }
}
