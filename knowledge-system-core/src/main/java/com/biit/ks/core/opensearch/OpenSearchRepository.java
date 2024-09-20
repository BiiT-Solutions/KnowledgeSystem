package com.biit.ks.core.opensearch;

import com.biit.ks.core.opensearch.search.MustHavePredicates;
import org.apache.commons.lang3.NotImplementedException;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public abstract class OpenSearchRepository<T extends OpenSearchItem<ID>, ID> implements JpaRepository<T, ID> {
    private static final Integer MAX_QUERY_ELEMENTS = 10000;

    private static final String INDEX = "/repository";

    private final OpenSearchClient openSearchClient;
    private final Class<T> clazz;

    public OpenSearchRepository(Class<T> clazz, OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
        this.clazz = clazz;
    }

    @Override
    public void flush() {
        openSearchClient.refreshIndex();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        try {
            return save(entity);
        } finally {
            flush();
        }
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        try {
            return saveAll(entities);
        } finally {
            flush();
        }
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        this.deleteAll(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        ids.forEach(id -> {
            openSearchClient.deleteData(INDEX, String.valueOf(id));
        });
    }

    @Override
    public void deleteAllInBatch() {
        this.deleteAll();
    }

    @Override
    public T getOne(ID id) {
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        mustHavePredicates.add("id", String.valueOf(id));
        final SearchResponse<T> response = openSearchClient.searchData(new SearchQuery<>(clazz, mustHavePredicates));
        if (!response.hits().hits().isEmpty()) {
            return response.hits().hits().get(0).source();
        }
        return null;
    }

    @Override
    public T getById(ID id) {
        return getOne(id);
    }

    @Override
    public T getReferenceById(ID id) {
        return getById(id);
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new NotImplementedException();
    }

    @Override
    public <S extends T> S save(S entity) {
        openSearchClient.indexData(clazz, INDEX, entity.getId() + "");
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        entities.forEach(this::save);
        final List<S> result = new ArrayList<S>();
        entities.forEach(result::add);
        return result;
    }

    @Override
    public Optional<T> findById(ID id) {
        final T result = getById(id);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    public boolean existsById(ID id) {
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        mustHavePredicates.add("id", String.valueOf(id));
        final CountResponse response = openSearchClient.countData(new SearchQuery<>(clazz, mustHavePredicates));
        return response.count() > 0;
    }

    @Override
    public List<T> findAll() {
        final SearchResponse<T> response = openSearchClient.searchData(new SearchQuery<T>(clazz, 0, MAX_QUERY_ELEMENTS));
        final List<T> result = new ArrayList<T>();
        response.hits().hits().forEach(tHit -> result.add(tHit.source()));
        return result;
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        final StringBuilder stringBuilder = new StringBuilder();
        ids.forEach(id -> {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(" OR ");
            }
            stringBuilder.append(id);
        });
        mustHavePredicates.add("id", stringBuilder.toString());
        final SearchResponse<T> response = openSearchClient.searchData(new SearchQuery<>(clazz, mustHavePredicates));
        final List<T> results = new ArrayList<>();
        if (!response.hits().hits().isEmpty()) {
            response.hits().hits().forEach(tHit -> results.add(tHit.source()));
        }
        return results;
    }

    @Override
    public long count() {
        final CountResponse response = openSearchClient.countData(new SearchQuery<T>(clazz));
        return response.count();

//        final SearchResponse<T> response = openSearchClient.searchData(new SearchQuery<T>(clazz, 0, 10000));
//        return response.hits().hits().size();
    }

    @Override
    public void deleteById(ID id) {
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        mustHavePredicates.add("id", String.valueOf(id));
        openSearchClient.deleteData(new SearchQuery<>(clazz, mustHavePredicates));
    }

    @Override
    public void delete(T entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        openSearchClient.deleteIndex(INDEX);
    }

    @Override
    public List<T> findAll(Sort sort) {
        throw new NotImplementedException();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        final SearchResponse<T> response = openSearchClient.searchData(new SearchQuery<T>(clazz, pageable.getPageNumber(), pageable.getPageSize()));
        final List<T> result = new ArrayList<T>();
        response.hits().hits().forEach(tHit -> result.add(tHit.source()));
        return new PageImpl<>(result, pageable, response.hits().hits().size());
    }
}
