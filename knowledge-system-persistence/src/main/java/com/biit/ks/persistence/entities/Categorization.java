package com.biit.ks.persistence.entities;

import java.util.UUID;

public class Categorization extends OpenSearchElement<UUID> {

    private UUID uuid;

    public Categorization() {
        super();
        uuid = UUID.randomUUID();
    }

    public Categorization(String name) {
        this();
        setName(name);
    }


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

    @Override
    public String toString() {
        return "Categorization{"
                + "name='" + getName() + '\''
                + '}';
    }
}
