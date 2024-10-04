package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;

public class OpenSearchElementProvider<E extends OpenSearchElement<?>> {

    private final OpenSearchElementRepository<E> openSearchElementRepository;

    public OpenSearchElementProvider(OpenSearchElementRepository<E> openSearchElementRepository) {
        this.openSearchElementRepository = openSearchElementRepository;
    }


    public OpenSearchElementRepository<E> getRepository() {
        return openSearchElementRepository;
    }
}
