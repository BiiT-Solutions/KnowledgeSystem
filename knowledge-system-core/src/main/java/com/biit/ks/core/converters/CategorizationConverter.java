package com.biit.ks.core.converters;


import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.models.CategorizationDTO;
import com.biit.ks.persistence.entities.Categorization;
import com.biit.server.controller.converters.SimpleConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CategorizationConverter extends SimpleConverter<Categorization, CategorizationDTO, CategorizationConverterRequest> {

    @Override
    protected CategorizationDTO convertElement(CategorizationConverterRequest from) {
        final CategorizationDTO formDTO = new CategorizationDTO();
        BeanUtils.copyProperties(from.getEntity(), formDTO);
        return formDTO;
    }

    @Override
    public Categorization reverse(CategorizationDTO to) {
        if (to == null) {
            return null;
        }
        final Categorization form = new Categorization();
        BeanUtils.copyProperties(to, form);
        return form;
    }
}
