package com.biit.ks.core.converters;

import com.biit.ks.core.converters.models.FormRulesConverterRequest;
import com.biit.ks.core.models.FormRulesDTO;
import com.biit.ks.persistence.entities.FormRules;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;

public class FormRulesConverter extends ElementConverter<FormRules, FormRulesDTO, FormRulesConverterRequest> {

    @Override
    protected FormRulesDTO convertElement(FormRulesConverterRequest from) {
        final FormRulesDTO formRulesDTO = new FormRulesDTO();
        BeanUtils.copyProperties(from.getEntity(), formRulesDTO);
        return formRulesDTO;
    }

    @Override
    public FormRules reverse(FormRulesDTO to) {
        if (to == null) {
            return null;
        }
        final FormRules formRules = new FormRules();
        BeanUtils.copyProperties(to, formRules);
        return formRules;
    }
}
