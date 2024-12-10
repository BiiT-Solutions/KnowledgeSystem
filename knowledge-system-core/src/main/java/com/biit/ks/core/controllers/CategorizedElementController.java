package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.CategorizedElementConverter;
import com.biit.ks.core.converters.models.CategorizedElementConverterRequest;
import com.biit.ks.core.providers.CategorizedElementProvider;
import com.biit.ks.dto.CategorizedElementDTO;
import com.biit.ks.persistence.entities.CategorizedElement;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.intervals.QuantifiersOperator;
import com.biit.ks.persistence.repositories.CategorizedElementRepository;

import java.util.Collection;

public abstract class CategorizedElementController<
        E extends CategorizedElement<?>,
        D extends CategorizedElementDTO<?>,
        R extends CategorizedElementRepository<E>,
        P extends CategorizedElementProvider<E, R>,
        Rq extends CategorizedElementConverterRequest<E>,
        Cv extends CategorizedElementConverter<E, D, Rq>>
        extends OpenSearchElementController<E, D, R, P, Rq, Cv> {


    protected CategorizedElementController(P provider, Cv converter) {
        super(provider, converter);
    }

    public SearchWrapper<D> searchByCategories(Collection<String> categorizations, QuantifiersOperator quantifiersOperator, Integer from, Integer size) {
        return convertAll(getProvider().searchByCategoryNames(categorizations, quantifiersOperator, from, size));
    }

    public long countByCategories(Collection<String> categorizations, QuantifiersOperator quantifiersOperator) {
        return getProvider().countByCategoryNames(categorizations, quantifiersOperator);
    }
}
