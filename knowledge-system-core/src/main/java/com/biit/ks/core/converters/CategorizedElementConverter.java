package com.biit.ks.core.converters;

import com.biit.ks.core.converters.models.CategorizationConverterRequest;
import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.dto.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;

import java.util.ArrayList;

public abstract class CategorizedElementConverter<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        Rq extends CategorizedElementConverterRequest<E>>
        extends OpenSearchElementConverter<E, D, Rq> {

    private final CategorizationConverter categorizationConverter;

    protected CategorizedElementConverter(CategorizationConverter categorizationConverter) {
        this.categorizationConverter = categorizationConverter;
    }

    public void copyCategorizations(E from, D to) {
        to.setCategorizations(new ArrayList<>());
        if (from.getCategorizations() != null) {
            from.getCategorizations().forEach(categorization -> to.getCategorizations().add(
                    categorizationConverter.convert(new CategorizationConverterRequest(categorization))));
        }
    }


    public void copyCategorizations(D from, E to) {
        to.setCategorizations(new ArrayList<>());
        if (from.getCategorizations() != null) {
            from.getCategorizations().forEach(categorizationDTO -> to.getCategorizations().add(
                    categorizationConverter.reverse(categorizationDTO)));
        }
    }
}
