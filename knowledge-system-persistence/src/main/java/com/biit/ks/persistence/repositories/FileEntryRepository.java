package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.OpenSearchClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class FileEntryRepository {

    public static final String OPENSEARCH_INDEX = "file-index";

    private final OpenSearchClient openSearchClient;

    public FileEntryRepository(OpenSearchClient openSearchClient) {
        this.openSearchClient = openSearchClient;
    }

    public FileEntry save(FileEntry fileEntry) {
        openSearchClient.indexData(fileEntry, OPENSEARCH_INDEX, fileEntry.getUuid() != null ? fileEntry.getUuid().toString() : null);
        return fileEntry;
    }


    public Optional<FileEntry> findFileEntryByFilePathAndFileName(String realFilePath, String fileName) {
        return Optional.empty();
    }

    public Optional<FileEntry> get(UUID uuid) {
        return Optional.empty();
    }
}
