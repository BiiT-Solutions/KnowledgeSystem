package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.FileEntryConverter;
import com.biit.ks.core.converters.models.FileEntryConverterRequest;
import com.biit.ks.core.exceptions.FileAlreadyExistsException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.models.Chunk;
import com.biit.ks.core.exceptions.SeaweedClientException;
import com.biit.ks.core.files.MediaTypeCalculator;
import com.biit.ks.core.models.ChunkData;
import com.biit.ks.core.models.FileEntryDTO;
import com.biit.ks.core.opensearch.OpenSearchClient;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.core.seaweed.SeaweedClient;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import com.biit.server.controller.ElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import seaweedfs.client.SeaweedInputStream;

import java.io.IOException;
import java.util.UUID;

@Controller
public class FileEntryController extends ElementController<FileEntry, UUID, FileEntryDTO, FileEntryRepository,
        FileEntryProvider, FileEntryConverterRequest, FileEntryConverter> {

    private static final String OPENSEARCH_INDEX = "file-index";

    private final SeaweedClient seaweedClient;
    private final IAuthenticatedUserProvider authenticatedUserProvider;
    private final OpenSearchClient openSearchClient;


    @Autowired
    protected FileEntryController(FileEntryProvider provider, FileEntryConverter converter, SeaweedClient seaweedClient,
                                  IAuthenticatedUserProvider authenticatedUserProvider, OpenSearchClient openSearchClient) {
        super(provider, converter);
        this.seaweedClient = seaweedClient;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.openSearchClient = openSearchClient;
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

    public ChunkData downloadChunk(String filePath, long skip, int size) {
        final FileEntry fileEntry = getProvider().findByFilePath(filePath)
            .orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with path '" + filePath + "'."));
        try {
            return new ChunkData(seaweedClient.getChunk(filePath, skip, size), fileEntry.getMimeType());
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

    public FileEntryDTO upload(MultipartFile file, FileEntryDTO fileEntryDTO, Boolean forceRewrite, String createdBy) {
        try {
            if (fileEntryDTO == null) {
                fileEntryDTO = new FileEntryDTO();
            }
            setFields(fileEntryDTO, file, createdBy);
            if (forceRewrite == null || !forceRewrite) {
                //Check if file already exists.
                checkExistingFile(fileEntryDTO);
            }
            try {
                seaweedClient.addFile(fileEntryDTO.getCompleteFilePath(), file);
                //Save it on Opensearch
                openSearchClient.indexData(fileEntryDTO, OPENSEARCH_INDEX, fileEntryDTO.getUuid().toString());
                return fileEntryDTO;
            } catch (IOException e) {
                throw new SeaweedClientException(this.getClass(), e);
            }
        } catch (DataIntegrityViolationException e) {
            throw new FileAlreadyExistsException(this.getClass(), e);
        }
    }


    private void setFields(FileEntryDTO fileEntryDTO, MultipartFile file, String createdBy) {
        fileEntryDTO.setMimeType(MediaTypeCalculator.getRealMimeType(file));
        fileEntryDTO.setFileName(file.getOriginalFilename());
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(createdBy).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No User with username '" + createdBy + "' found on the system."));
        fileEntryDTO.setCreatedBy(user.getUID());
        fileEntryDTO.setFilePath("/uploads/" + user.getUID());
    }


    private void checkExistingFile(FileEntryDTO fileEntryDTO) {
        if (seaweedClient.getEntry(fileEntryDTO.getFilePath(), fileEntryDTO.getFileName()) != null) {
            throw new FileAlreadyExistsException(this.getClass(), "File '" + fileEntryDTO + "' already exists.");
        }
    }


}
