package com.biit.ks.core.providers;


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileEntryProvider extends ElementProvider<FileEntry, UUID, FileEntryRepository> {


    @Autowired
    public FileEntryProvider(FileEntryRepository repository) {
        super(repository);
    }

    public Optional<FileEntry> findByFilePath(String filePath) {
        final File f = new File(filePath);
        final String realFilePath = f.getParent();
        final String fileName = f.getName();
        return getRepository().findFileEntryByFilePathAndFileName(realFilePath, fileName);
    }
}
