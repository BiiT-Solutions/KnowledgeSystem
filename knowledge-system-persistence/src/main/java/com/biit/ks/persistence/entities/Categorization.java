package com.biit.ks.persistence.entities;

import java.util.UUID;

public class Categorization extends OpenSearchElement<UUID> {

    private UUID uuid;
    private String name;


    @Override
    public UUID getId() {
        return getUuid();
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setId(UUID id) {
        setUuid(id);
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
