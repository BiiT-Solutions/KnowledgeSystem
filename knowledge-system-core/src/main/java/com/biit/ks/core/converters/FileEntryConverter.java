package com.biit.ks.core.converters;


import com.biit.ks.core.converters.models.FileEntryConverterRequest;
import com.biit.ks.core.models.FileEntryDTO;
import com.biit.ks.persistence.entities.FileEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class FileEntryConverter extends CategorizedElementConverter<FileEntry, FileEntryDTO, FileEntryConverterRequest> {

    @Override
    protected FileEntryDTO convertElement(FileEntryConverterRequest from) {
        final FileEntryDTO formDTO = new FileEntryDTO();
        BeanUtils.copyProperties(from.getEntity(), formDTO);
        return formDTO;
    }

    @Override
    public FileEntry reverse(FileEntryDTO to) {
        if (to == null) {
            return null;
        }
        final FileEntry form = new FileEntry();
        BeanUtils.copyProperties(to, form);
        return form;
    }
}
