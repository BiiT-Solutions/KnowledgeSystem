package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import com.biit.ks.persistence.opensearch.search.Fuzziness;
import com.biit.ks.persistence.opensearch.search.FuzzinessDefinition;
import com.biit.ks.persistence.opensearch.search.MustHavePredicates;
import com.biit.ks.persistence.opensearch.search.ShouldHavePredicates;
import jakarta.annotation.PostConstruct;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileEntryRepository extends CategorizedElementRepository<FileEntry> {

    public static final String OPENSEARCH_INDEX = "file-index";

    private final OpenSearchClient openSearchClient;

    public FileEntryRepository(OpenSearchClient openSearchClient) {
        super(FileEntry.class, openSearchClient);
        this.openSearchClient = openSearchClient;
    }

    @PostConstruct
    public void createIndex() {
        try {
            openSearchClient.createIndex(OPENSEARCH_INDEX);
        } catch (OpenSearchException e) {
            if (!e.getMessage().contains("resource_already_exists_exception")) {
                throw e;
            }
        }
    }

    public FileEntry save(FileEntry fileEntry) {
        openSearchClient.indexData(fileEntry, OPENSEARCH_INDEX, fileEntry.getUuid() != null ? fileEntry.getUuid().toString() : null);
        return fileEntry;
    }


    public Optional<FileEntry> findFileEntryByFilePathAndFileName(String filePath, String fileName) {
        if (filePath == null || fileName == null) {
            return Optional.empty();
        }
        final MustHavePredicates mustHaveParameters = new MustHavePredicates();
        mustHaveParameters.add("filePath", filePath);
        mustHaveParameters.add("fileName", fileName);
        openSearchClient.getAll(FileEntry.class, OPENSEARCH_INDEX);
        final SearchResponse<FileEntry> response = openSearchClient.searchData(FileEntry.class, mustHaveParameters);
        if (response == null || response.hits() == null || response.hits().hits().isEmpty() || response.hits().hits().get(0) == null
                || response.hits().hits().get(0).source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.hits().hits().get(0).source());
    }


    public Optional<FileEntry> get(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        final GetResponse<FileEntry> response = openSearchClient.getData(FileEntry.class, OPENSEARCH_INDEX, uuid.toString());
        if (response == null || response.source() == null) {
            return Optional.empty();
        }
        return Optional.of(response.source());
    }

    public List<FileEntry> search(String query, Integer from, Integer size) {
        final ShouldHavePredicates shouldHavePredicates = new ShouldHavePredicates();
        shouldHavePredicates.add(Pair.of("description", query));
        shouldHavePredicates.add(Pair.of("fileName", query));
        shouldHavePredicates.add(Pair.of("fileFormat", query));
        shouldHavePredicates.add(Pair.of("mimeType", query));
        shouldHavePredicates.setFuzzinessDefinition(new FuzzinessDefinition(Fuzziness.AUTO));
        shouldHavePredicates.setMinimumShouldMatch(1);
        final SearchResponse<FileEntry> response = openSearchClient.searchData(FileEntry.class, shouldHavePredicates, from, size);
        return openSearchClient.convertResponse(response);

    }
}
