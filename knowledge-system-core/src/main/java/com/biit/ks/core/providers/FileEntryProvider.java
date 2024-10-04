package com.biit.ks.core.providers;


import com.biit.ks.core.providers.pools.FileEntryByStringPool;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileEntryProvider extends CategorizedElementProvider<FileEntry, FileEntryRepository> {

    private final FileEntryByStringPool fileEntryByStringPool;

    @Autowired
    public FileEntryProvider(FileEntryRepository fileEntryRepository, FileEntryByStringPool fileEntryByStringPool) {
        super(fileEntryRepository);
        this.fileEntryByStringPool = fileEntryByStringPool;
    }


    public FileEntry save(FileEntry fileEntry) {
        return getRepository().save(fileEntry);
    }

    public List<FileEntry> search(String searchQuery, Integer from, Integer size) {
        return getRepository().search(searchQuery, from, size);
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
