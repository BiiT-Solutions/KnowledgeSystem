package com.biit.ks.persistence.entities;

import java.util.List;

public abstract class CategorizedElement<U> extends Element<U> {

    private List<Categorization> categorizations;

    public List<Categorization> getCategorizations() {
        return categorizations;
    }

    public void setCategorizations(List<Categorization> categorizations) {
        this.categorizations = categorizations;
    }
}
