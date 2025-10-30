package com.biit.ks.persistence.entities;

/*-
 * #%L
 * Knowledge System (Persistence)
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Text extends CategorizedElement<UUID> {

    private UUID uuid;

    //Content by language.
    private Map<TextLanguages, String> content;

    public Text() {
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
