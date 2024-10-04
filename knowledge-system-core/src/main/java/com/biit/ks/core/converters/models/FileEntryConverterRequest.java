package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.FileEntry;

import java.util.Optional;

public class FileEntryConverterRequest extends CategorizedElementConverterRequest<FileEntry> {

    public FileEntryConverterRequest(FileEntry entity) {
        super(entity);
    }

    public FileEntryConverterRequest(Optional<FileEntry> entity) {
        super(entity);
    }
}
