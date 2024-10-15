package com.biit.ks.core.providers;

import com.biit.ks.core.providers.pools.OpenSearchElementPool;
import com.biit.ks.persistence.entities.OpenSearchElement;
import com.biit.ks.persistence.repositories.OpenSearchElementRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OpenSearchElementProvider<E extends OpenSearchElement<?>, R extends OpenSearchElementRepository<E>> {

    private final OpenSearchElementPool<E> openSearchElementPool;
    private final R openSearchElementRepository;

    public OpenSearchElementProvider(OpenSearchElementPool<E> openSearchElementPool, R openSearchElementRepository) {
        this.openSearchElementPool = openSearchElementPool;
        this.openSearchElementRepository = openSearchElementRepository;
    }


    public R getRepository() {
        return openSearchElementRepository;
    }

    public OpenSearchElementPool<E> getPool() {
        return openSearchElementPool;
    }

    public E save(E element) {
        return getRepository().save(element);
    }


    public E update(E element) {
        return getRepository().update(element);
    }


    public void delete(E element) {
        getRepository().delete(element);
    }


    public void delete(UUID id) {
        getRepository().delete(id);
    }


    public List<E> search(String searchQuery, Integer from, Integer size) {
        return getRepository().search(searchQuery, from, size);
    }


    public Optional<E> get(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        final E cached = openSearchElementPool.getElement(uuid.toString());
        if (cached != null) {
            return Optional.of(cached);
        }
        final Optional<E> saved = getRepository().get(uuid);
        saved.ifPresent(fileEntry -> openSearchElementPool.addElement(fileEntry, uuid.toString()));
        return saved;
    }

    public List<E> getAll(Integer from, Integer size) {
        return getRepository().getAll(from, size);
    }
}
