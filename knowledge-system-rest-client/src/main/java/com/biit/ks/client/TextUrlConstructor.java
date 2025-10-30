package com.biit.ks.client;

/*-
 * #%L
 * Knowledge System (Rest Client)
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
