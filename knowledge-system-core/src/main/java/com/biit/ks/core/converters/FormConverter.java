package com.biit.ks.core.converters;


import com.biit.ks.core.converters.models.FormConverterRequest;
import com.biit.ks.core.models.FormDTO;
import com.biit.ks.persistence.entities.Form;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class FormConverter extends ElementConverter<Form, FormDTO, FormConverterRequest> {

    @Override
    protected FormDTO convertElement(FormConverterRequest from) {
        final FormDTO formDTO = new FormDTO();
        BeanUtils.copyProperties(from.getEntity(), formDTO);
        return formDTO;
    }

    @Override
    public Form reverse(FormDTO to) {
        if (to == null) {
            return null;
        }
        final Form form = new Form();
        BeanUtils.copyProperties(to, form);
        return form;
    }
}
