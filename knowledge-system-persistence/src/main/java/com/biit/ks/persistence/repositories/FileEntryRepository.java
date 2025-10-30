package com.biit.ks.persistence.repositories;

/*-
 * #%L
 * Knowledge System (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.opensearch.search.SearchPredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SimpleSearch;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch.core.CountResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public class FileEntryRepository extends CategorizedElementRepository<FileEntry> {

    private final IOpenSearchConfigurator openSearchConfigurator;


    public FileEntryRepository(OpenSearchClient openSearchClient, IOpenSearchConfigurator openSearchConfigurator) {
        super(FileEntry.class, openSearchClient);
        this.openSearchConfigurator = openSearchConfigurator;
    }

    @Override
    public String getOpenSearchIndex() {
        return openSearchConfigurator.getOpenSearchFileIndex();
    }


    public Optional<FileEntry> findFileEntryByFilePathAndFileName(String filePath, String fileName) {
        if (filePath == null || fileName == null) {
            return Optional.empty();
        }
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.add("filePath", filePath);
        mustHaveParameters.add("name", fileName);
        final SearchResponse<FileEntry> response = getOpenSearchClient().searchData(FileEntry.class, getOpenSearchIndex(), mustHaveParameters);
        if (response == null || response.hits() == null || response.hits().hits().isEmpty() || response.hits().hits().get(0) == null
                || response.hits().hits().get(0).source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.hits().hits().get(0).source());
    }


    public SearchWrapper<FileEntry> findFileEntriesWithThumbnailIsNull() {
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.add("thumbnailUrl", null);
        final SearchResponse<FileEntry> response = getOpenSearchClient().searchData(FileEntry.class, getOpenSearchIndex(), mustHaveParameters);
        if (response == null || response.hits() == null || response.hits().hits().isEmpty() || response.hits().hits().get(0) == null
                || response.hits().hits().get(0).source() == null) {
            return new SearchWrapper<>(new ArrayList<>());
        }
        return getOpenSearchClient().convertResponse(response);
    }


    @Override
    public SearchPredicates searchByValuePredicate(String value, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("alias", value));
        shouldHavePredicates.add(Pair.of("description", value));
        shouldHavePredicates.add(Pair.of("name", value));
        shouldHavePredicates.add(Pair.of("fileFormat", value));
        shouldHavePredicates.add(Pair.of("mimeType", value));
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        return shouldHavePredicates;
    }

    public void deleteByAlias(String alias) {
        final MustHavePredicates mustHavePredicates = new MustHavePredicates();
        mustHavePredicates.add(Pair.of("alias", alias));
        getOpenSearchClient().deleteData(FileEntry.class, getOpenSearchIndex(), mustHavePredicates);
    }


    public SearchWrapper<FileEntry> findFileEntryByAlias(String alias, Integer from, Integer size) {
        if (alias == null) {
            return new SearchWrapper<>(new ArrayList<>());
        }
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.add("alias", alias);
        final SearchResponse<FileEntry> response = getOpenSearchClient().searchData(FileEntry.class, getOpenSearchIndex(), mustHaveParameters, from, size);
        if (response == null || response.hits() == null || response.hits().hits().isEmpty() || response.hits().hits().get(0) == null
                || response.hits().hits().get(0).source() == null) {
            return new SearchWrapper<>(new ArrayList<>());
        }
        return getOpenSearchClient().convertResponse(response);
    }


    public long countFileEntryByAlias(String alias) {
        if (alias == null) {
            return 0;
        }
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.add("alias", alias);
        final CountResponse response = getOpenSearchClient().countData(FileEntry.class, getOpenSearchIndex(), mustHaveParameters);
        return getOpenSearchClient().convertResponse(response);
    }

    @Override
    protected ShouldHavePredicates convertSearch(SimpleSearch searchQuery) {
        final ShouldHavePredicates shouldHavePredicates = super.convertSearch(searchQuery);
        if (searchQuery.getContent() != null && !searchQuery.getContent().isBlank()) {
            shouldHavePredicates.add(Pair.of("alias", searchQuery.getContent()));
        }
        if (searchQuery.getType() != null && !searchQuery.getType().isBlank()) {
            shouldHavePredicates.add("mimeType", searchQuery.getType());
        }
        return shouldHavePredicates;
    }
}
