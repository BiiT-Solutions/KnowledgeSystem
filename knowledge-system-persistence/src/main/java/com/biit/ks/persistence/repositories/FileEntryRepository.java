package com.biit.ks.persistence.repositories;


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface FileEntryRepository extends ElementRepository<FileEntry, UUID> {

    Optional<FileEntry> findFileEntryByFilePathAndFileName(String filePath, String fileName);
}
