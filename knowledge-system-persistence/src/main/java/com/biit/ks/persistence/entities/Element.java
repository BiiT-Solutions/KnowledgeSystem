package com.biit.ks.persistence.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Element<KEY> {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String createdByHash;
    private String updatedBy;
    private String updatedByHash;


    public abstract KEY getId();

    public abstract void setId(KEY id);

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByHash() {
        return createdByHash;
    }

    public void setCreatedByHash(String createdByHash) {
        this.createdByHash = createdByHash;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedByHash() {
        return updatedByHash;
    }

    public void setUpdatedByHash(String updatedByHash) {
        this.updatedByHash = updatedByHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Element<?> element = (Element<?>) o;
        return Objects.equals(getId(), element.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
