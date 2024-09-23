package com.biit.ks.core.converters;

import com.biit.ks.persistence.entities.Element;
import com.biit.server.controller.converters.SimpleConverter;
import com.biit.server.controllers.models.CreatedElementDTO;
import com.biit.server.converters.models.ConverterRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ElementConverter<ENTITY extends Element<?>, DTO extends CreatedElementDTO, REQUEST extends ConverterRequest<ENTITY>>
        extends SimpleConverter<ENTITY, DTO, REQUEST> {
    public ElementConverter() {
    }

    protected abstract DTO convertElement(REQUEST from);

    public DTO convert(REQUEST from) {
        return from != null && from.hasEntity() ? this.convertElement(from) : null;
    }

    @Override
    public List<DTO> convertAll(Collection<REQUEST> from) {
        if (from == null) {
            return new ArrayList<>();
        }
        return from.stream().map(this::convert).sorted(Comparator.comparing(CreatedElementDTO::getCreatedAt,
                Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    @Override
    public List<ENTITY> reverseAll(Collection<DTO> to) {
        if (to == null) {
            return new ArrayList<>();
        }
        return to.stream().map(this::reverse).collect(Collectors.toList());
    }
}
