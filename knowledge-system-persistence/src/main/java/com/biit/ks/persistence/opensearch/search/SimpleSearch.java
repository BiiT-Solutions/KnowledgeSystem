package com.biit.ks.persistence.opensearch.search;

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

import java.time.LocalDateTime;
import java.util.List;

public class SimpleSearch {
    private String content;
    private String type;
    private String owner;
    private LocalDateTime from;
    private LocalDateTime to;
    private List<String> keywords;

    public SimpleSearch() {
        super();
    }

    public SimpleSearch(String content, String type, String owner, LocalDateTime from, LocalDateTime to, List<String> keywords) {
        this();
        this.content = content;
        this.type = type;
        this.owner = owner;
        this.from = from;
        this.to = to;
        this.keywords = keywords;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public int getRequiredQueries() {
        int counter = 0;
        if (getContent() != null && !getContent().isBlank()) {
            counter++;
        }
        if (getType() != null && !getType().isBlank()) {
            counter++;
        }
        if (getOwner() != null && !getOwner().isBlank()) {
            counter++;
        }
        if (getFrom() != null || getTo() != null) {
            counter++;
        }
        if (getKeywords() != null) {
            counter += getKeywords().size();
        }
        return counter;
    }
}
