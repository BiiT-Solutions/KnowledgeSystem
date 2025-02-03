package com.biit.ks.client;

import com.biit.ks.client.exceptions.InvalidConfigurationException;
import com.biit.ks.dto.TextDTO;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.models.ITextClient;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.InvalidResponseException;
import com.biit.server.client.SecurityClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Order(2)
@Qualifier("textClient")
public class TextClient implements ITextClient {

    private final TextUrlConstructor textUrlConstructor;

    private final SecurityClient securityClient;

    private final ObjectMapper mapper;

    public TextClient(TextUrlConstructor textUrlConstructor, SecurityClient securityClient, ObjectMapper mapper) {
        this.textUrlConstructor = textUrlConstructor;
        this.securityClient = securityClient;
        this.mapper = mapper;
    }


    @Override
    public Optional<TextDTO> get(UUID uuid) {
        try {
            try (Response response = securityClient.get(textUrlConstructor.getKnowledgeSystemServerUrl(),
                    textUrlConstructor.getTextByUUID(uuid))) {
                KnowledgeSystemLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        textUrlConstructor.getKnowledgeSystemServerUrl() + textUrlConstructor.getTextByUUID(uuid),
                        response.getStatus());
                return Optional.of(mapper.readValue(response.readEntity(String.class), TextDTO.class));
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        } catch (EmptyResultException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            KnowledgeSystemLogger.warning(this.getClass(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> get(UUID uuid, String language) {
        try {
            try (Response response = securityClient.get(textUrlConstructor.getKnowledgeSystemServerUrl(),
                    textUrlConstructor.getTextByUUIDAndLocale(uuid, language), MediaType.TEXT_PLAIN)) {
                KnowledgeSystemLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        textUrlConstructor.getKnowledgeSystemServerUrl() + textUrlConstructor.getTextByUUIDAndLocale(uuid, language),
                        response.getStatus());
                return Optional.of(response.readEntity(String.class));
            }
        } catch (EmptyResultException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            KnowledgeSystemLogger.warning(this.getClass(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> get(String textName, String language) {
        try {
            try (Response response = securityClient.get(textUrlConstructor.getKnowledgeSystemServerUrl(),
                    textUrlConstructor.getTextByNameAndLocale(textName, language), MediaType.TEXT_PLAIN)) {
                KnowledgeSystemLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        textUrlConstructor.getKnowledgeSystemServerUrl() + textUrlConstructor.getTextByNameAndLocale(textName, language),
                        response.getStatus());
                return Optional.of(response.readEntity(String.class));
            }
        } catch (EmptyResultException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            KnowledgeSystemLogger.warning(this.getClass(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> get(String textName, Locale locale) {
        return get(textName, locale.toLanguageTag());
    }
}
