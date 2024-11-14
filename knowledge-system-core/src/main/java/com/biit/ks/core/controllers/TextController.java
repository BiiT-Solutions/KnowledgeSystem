package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.TextConverter;
import com.biit.ks.core.converters.models.TextConverterRequest;
import com.biit.ks.core.models.TextDTO;
import com.biit.ks.core.providers.TextProvider;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.repositories.TextRepository;
import com.biit.server.logger.DtoControllerLogger;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Controller
public class TextController extends CategorizedElementController<Text, TextDTO, TextRepository,
        TextProvider, TextConverterRequest, TextConverter> {

    protected TextController(TextProvider provider, TextConverter converter) {
        super(provider, converter);
    }

    @Override
    protected TextConverterRequest createConverterRequest(Text text) {
        return new TextConverterRequest(text);
    }

    @Override
    public TextDTO create(TextDTO dto, String creatorName) {
        if (dto.getCreatedBy() == null && creatorName != null) {
            dto.setCreatedBy(creatorName);
        }
        validate(dto);
        final TextDTO dtoStored = convert(getProvider().save(getConverter().reverse(dto)));
        DtoControllerLogger.info(this.getClass(), "Text '{}' created by '{}'.", dtoStored, creatorName);
        return dtoStored;
    }

    @Override
    public Collection<TextDTO> create(Collection<TextDTO> textDTOS, String creatorName) {
        final List<TextDTO> results = new ArrayList<>();
        textDTOS.forEach(textDTO -> results.add(create(textDTO, creatorName)));
        return results;
    }
}
