package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import com.biit.ks.persistence.opensearch.search.SortOptionOrder;
import com.biit.ks.persistence.opensearch.search.SortResultOptions;
import org.apache.commons.lang3.tuple.Pair;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FileEntryRepository extends CategorizedElementRepository<FileEntry> {

    private final OpenSearchConfigurator openSearchConfigurator;


    public FileEntryRepository(OpenSearchClient openSearchClient, OpenSearchConfigurator openSearchConfigurator) {
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
        mustHaveParameters.add("fileName", fileName);
        final SearchResponse<FileEntry> response = getOpenSearchClient().searchData(FileEntry.class, mustHaveParameters);
        if (response == null || response.hits() == null || response.hits().hits().isEmpty() || response.hits().hits().get(0) == null
                || response.hits().hits().get(0).source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.hits().hits().get(0).source());
    }

    public List<FileEntry> findFileEntriesWithThumbnailIsNull() {
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.add("thumbnail", null);
        final SearchResponse<FileEntry> response = getOpenSearchClient().searchData(FileEntry.class, mustHaveParameters);
        if (response == null || response.hits() == null || response.hits().hits().isEmpty() || response.hits().hits().get(0) == null
                || response.hits().hits().get(0).source() == null) {
            return new ArrayList<>();
        }
        return getOpenSearchClient().convertResponse(response);
    }


    public List<FileEntry> search(String query, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("alias", query));
        shouldHavePredicates.add(Pair.of("description", query));
        shouldHavePredicates.add(Pair.of("fileName", query));
        shouldHavePredicates.add(Pair.of("fileFormat", query));
        shouldHavePredicates.add(Pair.of("mimeType", query));
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        final SearchResponse<FileEntry> response = getOpenSearchClient().searchData(FileEntry.class, shouldHavePredicates,
                new SortResultOptions("createdAt", SortOptionOrder.DESC), from, size);
        return getOpenSearchClient().convertResponse(response);

    }
}
