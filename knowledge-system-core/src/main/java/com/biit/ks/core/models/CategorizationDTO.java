package com.biit.ks.core.models;

import com.biit.server.controllers.models.ElementDTO;

import java.io.Serial;
import java.util.UUID;

public class CategorizationDTO extends ElementDTO<UUID> {

    @Serial
    private static final long serialVersionUID = -6257674089968981919L;

    private UUID uuid;
    private String name;

    public CategorizationDTO() {
        uuid = UUID.randomUUID();
    }

    public CategorizationDTO(String name) {
        this();
        this.name = name;
    }

    @Override
    public UUID getId() {
        return getUuid();
    }

    @Override
    public void setId(UUID id) {
        setUuid(id);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
