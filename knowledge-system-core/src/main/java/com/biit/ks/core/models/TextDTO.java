package com.biit.ks.core.models;

import com.biit.ks.persistence.entities.TextLanguages;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextDTO extends CategorizedElementDTO<UUID> {

    @Serial
    private static final long serialVersionUID = 4602223490653216436L;

    private UUID uuid;

    //Content by language.
    private Map<TextLanguages, String> content;

    public TextDTO() {
        super();
        setUuid(UUID.randomUUID());
    }


    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void setId(UUID id) {
        this.uuid = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Map<TextLanguages, String> getContent() {
        return content;
    }

    public void setContent(Map<TextLanguages, String> content) {
        this.content = content;
    }

    public void addContent(TextLanguages language, String content) {
        if (this.content == null) {
            this.content = new HashMap<>();
        }
        this.content.put(language, content);
    }
}
