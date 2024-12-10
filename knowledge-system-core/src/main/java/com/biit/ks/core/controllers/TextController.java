package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.TextConverter;
import com.biit.ks.core.converters.models.TextConverterRequest;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.exceptions.TextAlreadyExistsException;
import com.biit.ks.core.providers.TextProvider;
import com.biit.ks.dto.TextDTO;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.TextRepository;
import com.biit.server.exceptions.ValidateBadRequestException;
import com.biit.server.logger.DtoControllerLogger;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


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

    public SearchWrapper<TextDTO> getPublic(UUID uuid) {
        final SearchWrapper<Text> text = getProvider().get(uuid);

        if (!text.getFirst().isPublic()) {
            KnowledgeSystemLogger.warning(this.getClass(), "Trying to access to text '{}' using the public api. FileEntry is private!", uuid);
            //Same error as before.
            throw new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'.");
        }

        return convertAll(text);
    }

    public SearchWrapper<TextDTO> get(String name) {
        return convert(getProvider().get(name));
    }


    @Override
    public void validate(TextDTO dto) throws ValidateBadRequestException {
        if (dto.getName() != null) {
            final SearchWrapper<Text> existingText = getProvider().get(dto.getName());
            if (!existingText.isEmpty() && !Objects.equals(existingText.getFirst().getId(), dto.getId())) {
                throw new TextAlreadyExistsException(this.getClass(), "Already exists a text with name '" + dto.getName() + "'.");
            }
        }
    }
}
