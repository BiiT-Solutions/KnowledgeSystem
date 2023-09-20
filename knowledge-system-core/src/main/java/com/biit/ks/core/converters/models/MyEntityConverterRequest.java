package com.biit.ks.core.converters.models;

import com.biit.server.converters.models.ConverterRequest;
import com.biit.ks.persistence.entities.MyEntity;

public class MyEntityConverterRequest extends ConverterRequest<MyEntity> {
    public MyEntityConverterRequest(MyEntity myEntity) {
        super(myEntity);
    }
}
