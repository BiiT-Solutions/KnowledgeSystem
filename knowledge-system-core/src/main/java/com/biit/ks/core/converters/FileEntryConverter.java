package com.biit.ks.core.converters;


import com.biit.ks.core.converters.models.FileEntryConverterRequest;
import com.biit.ks.dto.FileEntryDTO;
import com.biit.ks.persistence.entities.FileEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class FileEntryConverter extends CategorizedElementConverter<FileEntry, FileEntryDTO, FileEntryConverterRequest> {

    public FileEntryConverter(CategorizationConverter categorizationConverter) {
        super(categorizationConverter);
    }


    @Override
    protected FileEntryDTO convertElement(FileEntryConverterRequest from) {
        if (from == null) {
            return null;
        }
        final FileEntryDTO fileEntryDTO = new FileEntryDTO();
        BeanUtils.copyProperties(from.getEntity(), fileEntryDTO);
        fileEntryDTO.setCategorizations(new ArrayList<>());
        copyCategorizations(from.getEntity(), fileEntryDTO);
        return fileEntryDTO;
    }


    @Override
    public FileEntry reverse(FileEntryDTO to) {
        if (to == null) {
            return null;
        }
        final FileEntry fileEntry = new FileEntry();
        BeanUtils.copyProperties(to, fileEntry);
        fileEntry.setCategorizations(new ArrayList<>());
        copyCategorizations(to, fileEntry);
        return fileEntry;
    }
}
