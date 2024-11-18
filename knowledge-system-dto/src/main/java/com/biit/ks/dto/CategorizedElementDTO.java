package com.biit.ks.dto;

import java.io.Serial;
import java.util.List;

public abstract class CategorizedElementDTO<U> extends OpenSearchElementDTO<U> {

    @Serial
    private static final long serialVersionUID = 7445445590960082105L;

    private List<CategorizationDTO> categorizations;

    public List<CategorizationDTO> getCategorizations() {
        return categorizations;
    }

    public void setCategorizations(List<CategorizationDTO> categorizations) {
        this.categorizations = categorizations;
    }
}
