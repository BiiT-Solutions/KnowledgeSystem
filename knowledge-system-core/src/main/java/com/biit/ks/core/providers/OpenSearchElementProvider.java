package com.biit.ks.core.providers;

import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;

public class OpenSearchElementProvider<E extends OpenSearchElement<?>, R extends OpenSearchElementRepository<E>> {

    private final R openSearchElementRepository;

    public OpenSearchElementProvider(R openSearchElementRepository) {
        this.openSearchElementRepository = openSearchElementRepository;
    }


    public R getRepository() {
        return openSearchElementRepository;
    }
}
