package com.biit.ks.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class ElementDTO<KEY> implements Serializable {

    @Serial
    private static final long serialVersionUID = 5311584213803465346L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public ElementDTO() {
        setCreatedAt(LocalDateTime.now());
    }

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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ElementDTO<?> element = (ElementDTO<?>) o;
        return Objects.equals(getId(), element.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
