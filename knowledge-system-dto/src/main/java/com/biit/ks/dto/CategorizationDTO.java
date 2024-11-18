package com.biit.ks.dto;

import java.io.Serial;
import java.util.UUID;

public class CategorizationDTO extends OpenSearchElementDTO<UUID> {

    @Serial
    private static final long serialVersionUID = -6257674089968981919L;

    private UUID uuid;

    public CategorizationDTO() {
        super();
        uuid = UUID.randomUUID();
    }

    public CategorizationDTO(String name) {
        super(name);
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
}
