package com.biit.ks.core.converters;

import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.dto.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;

public abstract class CategorizedElementConverter<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        Rq extends CategorizedElementConverterRequest<E>>
        extends OpenSearchElementConverter<E, D, Rq> {
}
