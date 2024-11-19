package com.biit.ks.client;

import com.biit.ks.client.exceptions.InvalidConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TextUrlConstructor {

    @Value("${knowledge.system.server.url:#{null}}")
    private String knowledgeSystemServerUrl;

    public String getKnowledgeSystemServerUrl() {
        if (knowledgeSystemServerUrl == null) {
            throw new InvalidConfigurationException(this.getClass(), "Value 'knowledge.system.server.url' not set on 'application.properties'!");
        }
        return knowledgeSystemServerUrl;
    }

    public String getTexts() {
        return "/texts";
    }

    public String getTextByNameAndLocale(String name, String language) throws InvalidConfigurationException {
        return "/texts/downloads/name/" + name + "/languages/" + language;
    }

    public String getTextByUUIDAndLocale(UUID uuid, String language) throws InvalidConfigurationException {
        return "/texts/downloads/" + uuid + "/languages/" + language;
    }

    public String getTextByUUID(UUID uuid) throws InvalidConfigurationException {
        return "/texts/" + uuid;
    }
}
