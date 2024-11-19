package com.biit.client;


import com.biit.ks.dto.TextDTO;
import com.biit.ks.dto.TextLanguagesDTO;
import com.biit.ks.models.ITextClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Primary
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@Qualifier("textClient")
public class TestTextClient implements ITextClient {

    private TextDTO content;

    public void setContent(TextDTO content) {
        this.content = content;
    }

    @Override
    public Optional<TextDTO> get(UUID uuid) {
        if (content != null && Objects.equals(content.getId(), uuid)) {
            return Optional.of(content);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> get(UUID uuid, String language) {
        if (content != null && Objects.equals(content.getId(), uuid)) {
            return Optional.of(content.getContent().get(TextLanguagesDTO.fromString(language)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> get(String textName, String language) {
        if (content != null && Objects.equals(content.getName(), textName)) {
            return Optional.of(content.getContent().get(TextLanguagesDTO.fromString(language)));
        }
        return Optional.empty();
    }
}
