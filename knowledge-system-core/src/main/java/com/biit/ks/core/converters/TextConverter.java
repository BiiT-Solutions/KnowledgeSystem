package com.biit.ks.core.converters;


import com.biit.ks.core.converters.models.TextConverterRequest;
import com.biit.ks.dto.TextDTO;
import com.biit.ks.dto.TextLanguagesDTO;
import com.biit.ks.persistence.entities.Text;
import com.biit.ks.persistence.entities.TextLanguages;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TextConverter extends CategorizedElementConverter<Text, TextDTO, TextConverterRequest> {

    @Override
    protected TextDTO convertElement(TextConverterRequest from) {
        if (from == null) {
            return null;
        }
        final TextDTO textDTO = new TextDTO();
        BeanUtils.copyProperties(from.getEntity(), textDTO);

        final Map<TextLanguagesDTO, String> content = new HashMap<>();
        from.getEntity().getContent().forEach((key, value) -> content.put(TextLanguagesDTO.fromString(key.name()), value));
        textDTO.setContent(content);
        return textDTO;
    }

    @Override
    public Text reverse(TextDTO to) {
        if (to == null) {
            return null;
        }
        final Text text = new Text();
        BeanUtils.copyProperties(to, text);
        final Map<TextLanguages, String> content = new HashMap<>();
        to.getContent().forEach((key, value) -> content.put(TextLanguages.fromString(key.name()), value));
        text.setContent(content);
        return text;
    }
}
