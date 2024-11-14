package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.Text;

import java.util.Optional;

public class TextConverterRequest extends CategorizedElementConverterRequest<Text> {

    public TextConverterRequest(Text entity) {
        super(entity);
    }

    public TextConverterRequest(Optional<Text> entity) {
        super(entity);
    }
}
