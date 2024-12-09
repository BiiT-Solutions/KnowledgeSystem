package com.biit.ks.persistence.opensearch.search;

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
