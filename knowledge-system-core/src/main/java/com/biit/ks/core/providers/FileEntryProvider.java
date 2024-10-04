package com.biit.ks.core.providers;


import com.biit.ks.core.providers.pools.OpenSearchElementPool;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class FileEntryProvider extends CategorizedElementProvider<FileEntry, FileEntryRepository> {

    private final FileEntryRepository fileEntryRepository;

    @Autowired
    public FileEntryProvider(OpenSearchElementPool<FileEntry> openSearchElementPool, FileEntryRepository fileEntryRepository) {
        super(openSearchElementPool, fileEntryRepository);
        this.fileEntryRepository = fileEntryRepository;
    }


    public Optional<FileEntry> findByFilePath(String filePath) {
        if (filePath == null) {
            return Optional.empty();
        }
        final FileEntry cached = getPool().getElement(filePath);
        if (cached != null) {
            return Optional.of(cached);
        }
        final File f = new File(filePath);
        final String realFilePath = f.getParent();
        final String fileName = f.getName();
        final Optional<FileEntry> saved = fileEntryRepository.findFileEntryByFilePathAndFileName(realFilePath, fileName);
        saved.ifPresent(fileEntry -> getPool().addElement(fileEntry, filePath));
        return saved;
    }
}
