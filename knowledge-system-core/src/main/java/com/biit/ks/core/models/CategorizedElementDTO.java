package com.biit.ks.core.models;

import com.biit.ks.persistence.entities.Categorization;

import java.io.Serial;
import java.util.List;

public abstract class CategorizedElementDTO<U> extends OpenSearchElementDTO<U> {

    @Serial
    private static final long serialVersionUID = 7445445590960082105L;

    private List<Categorization> categorizations;

    public List<Categorization> getCategorizations() {
        return categorizations;
    }

    public void setCategorizations(List<Categorization> categorizations) {
        this.categorizations = categorizations;
    }
}
