package com.biit.ks.core.converters;


import com.biit.ks.core.converters.models.TextConverterRequest;
import com.biit.ks.dto.TextDTO;
import com.biit.ks.persistence.entities.Text;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TextConverter extends CategorizedElementConverter<Text, TextDTO, TextConverterRequest> {

    @Override
    protected TextDTO convertElement(TextConverterRequest from) {
        final TextDTO textDTO = new TextDTO();
        BeanUtils.copyProperties(from.getEntity(), textDTO);
        return textDTO;
    }

    @Override
    public Text reverse(TextDTO to) {
        if (to == null) {
            return null;
        }
        final Text text = new Text();
        BeanUtils.copyProperties(to, text);
        return text;
    }
}
