package com.biit.ks.core.converters;

import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.core.models.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.server.controller.converters.SimpleConverter;

public abstract class CategorizedElementConverter<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        Rq extends CategorizedElementConverterRequest<E>>
        extends SimpleConverter<E, D, Rq> {
}
