package com.biit.ks.core.converters.models;


import com.biit.ks.persistence.entities.FileEntry;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Optional;

public class FileEntryConverterRequest extends ConverterRequest<FileEntry> {

    public FileEntryConverterRequest(FileEntry entity) {
        super(entity);
    }

    public FileEntryConverterRequest(Optional<FileEntry> entity) {
        super(entity);
    }
}
