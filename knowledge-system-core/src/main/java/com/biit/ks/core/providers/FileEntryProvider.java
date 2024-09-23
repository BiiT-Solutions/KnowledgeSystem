package com.biit.ks.core.providers;


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileEntryProvider {

    private final FileEntryRepository fileEntryRepository;

    @Autowired
    public FileEntryProvider(FileEntryRepository fileEntryRepository) {
        this.fileEntryRepository = fileEntryRepository;
    }

    private FileEntryRepository getRepository() {
        return fileEntryRepository;
    }

    public FileEntry save(FileEntry fileEntry) {
        return getRepository().save(fileEntry);
    }

    public Optional<FileEntry> get(UUID uuid) {
        return getRepository().get(uuid);
    }

    public Optional<FileEntry> findByFilePath(String filePath) {
        final File f = new File(filePath);
        final String realFilePath = f.getParent();
        final String fileName = f.getName();
        return getRepository().findFileEntryByFilePathAndFileName(realFilePath, fileName);
    }
}
