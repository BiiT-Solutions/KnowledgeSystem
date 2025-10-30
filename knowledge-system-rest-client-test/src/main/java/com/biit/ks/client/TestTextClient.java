package com.biit.ks.client;

/*-
 * #%L
 * Knowledge System (Rest Client Test)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.ks.dto.TextDTO;
import com.biit.ks.dto.TextLanguagesDTO;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.models.ITextClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Order(0)
@Qualifier("textClient")
public class TestTextClient implements ITextClient {

    private TextDTO content;

    public void setContent(TextDTO content) {
        this.content = content;
    }

    public TextDTO get() {
        return content;
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
            if (content.getContent() == null || content.getContent().get(TextLanguagesDTO.fromString(language)) == null) {
                KnowledgeSystemLogger.warning(this.getClass(), "No content available for test for language '{}'. Content: '{}'.", language, content);
            } else {
                return Optional.of(content.getContent().get(TextLanguagesDTO.fromString(language)));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> get(String textName, Locale locale) {
        return get(textName, locale.toLanguageTag());
    }
}
