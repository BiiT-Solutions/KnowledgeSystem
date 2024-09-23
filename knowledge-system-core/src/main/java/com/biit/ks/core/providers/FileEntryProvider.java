package com.biit.ks.core.providers;


import com.biit.ks.core.providers.pools.FileEntryByStringPool;
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
    private final FileEntryByStringPool fileEntryByStringPool;

    @Autowired
    public FileEntryProvider(FileEntryRepository fileEntryRepository, FileEntryByStringPool fileEntryByStringPool) {
        this.fileEntryRepository = fileEntryRepository;
        this.fileEntryByStringPool = fileEntryByStringPool;
    }

    private FileEntryRepository getRepository() {
        return fileEntryRepository;
    }


    public FileEntry save(FileEntry fileEntry) {
        return getRepository().save(fileEntry);
    }


    public Optional<FileEntry> get(UUID uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        final FileEntry cached = fileEntryByStringPool.getElement(uuid.toString());
        if (cached != null) {
            return Optional.of(cached);
        }
        final Optional<FileEntry> saved = getRepository().get(uuid);
        saved.ifPresent(fileEntry -> fileEntryByStringPool.addElement(fileEntry, uuid.toString()));
        return saved;
    }


    public Optional<FileEntry> findByFilePath(String filePath) {
        if (filePath == null) {
            return Optional.empty();
        }
        final FileEntry cached = fileEntryByStringPool.getElement(filePath);
        if (cached != null) {
            return Optional.of(cached);
        }
        final File f = new File(filePath);
        final String realFilePath = f.getParent();
        final String fileName = f.getName();
        final Optional<FileEntry> saved = getRepository().findFileEntryByFilePathAndFileName(realFilePath, fileName);
        saved.ifPresent(fileEntry -> fileEntryByStringPool.addElement(fileEntry, filePath));
        return saved;
    }
}
