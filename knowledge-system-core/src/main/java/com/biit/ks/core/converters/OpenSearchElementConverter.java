package com.biit.ks.core.converters;

import com.biit.ks.core.converters.models.OpenSearchElementConverterRequest;
import com.biit.ks.dto.OpenSearchElementDTO;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.server.controller.converters.SimpleConverter;

public abstract class OpenSearchElementConverter<
        E extends OpenSearchElement<?>,
        D extends OpenSearchElementDTO<?>,
        Rq extends OpenSearchElementConverterRequest<E>>
        extends SimpleConverter<E, D, Rq> {
}
