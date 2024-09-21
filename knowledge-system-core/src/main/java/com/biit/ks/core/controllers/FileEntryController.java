package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.FileEntryConverter;
import com.biit.ks.core.converters.models.FileEntryConverterRequest;
import com.biit.ks.core.exceptions.FileAlreadyExistsException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.Chunk;
import com.biit.ks.core.models.FileEntryDTO;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.core.seaweed.SeaweedClient;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import com.biit.server.controller.ElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import seaweedfs.client.SeaweedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Controller
public class FileEntryController extends ElementController<FileEntry, UUID, FileEntryDTO, FileEntryRepository,
        FileEntryProvider, FileEntryConverterRequest, FileEntryConverter> {

    private final SeaweedClient seaweedClient;


    @Autowired
    protected FileEntryController(FileEntryProvider provider, FileEntryConverter converter, SeaweedClient seaweedClient) {
        super(provider, converter);
        this.seaweedClient = seaweedClient;
    }


    @Override
    protected FileEntryConverterRequest createConverterRequest(FileEntry entity) {
        return new FileEntryConverterRequest(entity);
    }


    public Resource downloadAsResource(UUID uuid) {
        final FileEntry fileEntry =
                getProvider().get(uuid).orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'."));

        return downloadAsResource(fileEntry);
    }

    public Chunk downloadChunk(UUID uuid, long skip, int size) {
        final FileEntry fileEntry =
            getProvider().get(uuid).orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'."));

        return downloadChunk(fileEntry, skip, size);
    }


    public Resource downloadAsResource(String filePath) {
        final FileEntry fileEntry = getProvider().findByFilePath(filePath)
                .orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with path '" + filePath + "'."));
        return downloadAsResource(fileEntry);
    }

    public Chunk downloadChunk(String filePath, long skip, int size) {
        try {
            return seaweedClient.getChunk(filePath, skip, size);
        } catch (IOException e) {
            throw new FileNotFoundException(this.getClass(), "No file '" + filePath + "'.", e);
        }
    }


    private Resource downloadAsResource(FileEntry fileEntry) {
        try {
            final SeaweedInputStream seaweedInputStream = seaweedClient.getFile(fileEntry.getFilePath());
            return new InputStreamResource(seaweedInputStream);
        } catch (IOException e) {
            throw new FileNotFoundException(this.getClass(), "No file '" + fileEntry + "'.", e);
        }
    }

    private Chunk downloadChunk(FileEntry fileEntry, long skip, int size) {
        try {
            return seaweedClient.getChunk(fileEntry.getFilePath(), skip, size);
        } catch (IOException e) {
            throw new FileNotFoundException(this.getClass(), "No file '" + fileEntry + "'.", e);
        }
    }


    public FileEntryDTO upload(MultipartFile file, FileEntryDTO fileEntryDTO, String createdBy) {
        try {
            final FileEntry fileEntry = getProvider().save(reverse(fileEntryDTO));
            try {
                seaweedClient.addFile(fileEntry.getCompleteFilePath(), file);
                return convert(fileEntry);
            } catch (IOException e) {
                getProvider().delete(fileEntry);
                throw new RuntimeException(e);
            }
        } catch (DataIntegrityViolationException e) {
            throw new FileAlreadyExistsException(this.getClass(), e);
        }
    }


}
