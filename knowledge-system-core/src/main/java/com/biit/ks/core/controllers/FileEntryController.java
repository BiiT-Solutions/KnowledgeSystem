package com.biit.ks.core.controllers;

import com.biit.ks.core.converters.FileEntryConverter;
import com.biit.ks.core.converters.models.FileEntryConverterRequest;
import com.biit.ks.core.exceptions.FileAlreadyExistsException;
import com.biit.ks.core.exceptions.FileHandlingException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.exceptions.SeaweedClientException;
import com.biit.ks.core.files.MediaTypeCalculator;
import com.biit.ks.core.models.ChunkData;
import com.biit.ks.core.models.FileEntryDTO;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.core.seaweed.SeaweedClient;
import com.biit.ks.core.seaweed.SeaweedConfigurator;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchException;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.logger.DtoControllerLogger;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import seaweedfs.client.SeaweedInputStream;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Controller
public class FileEntryController extends CategorizedElementController<FileEntry, FileEntryDTO, FileEntryRepository,
        FileEntryProvider, FileEntryConverterRequest, FileEntryConverter> {


    private final SeaweedClient seaweedClient;
    private final IAuthenticatedUserProvider authenticatedUserProvider;
    private final FileEntryProvider fileEntryProvider;
    private final SeaweedConfigurator seaweedConfigurator;
    private final ThumbnailController thumbnailController;


    @Autowired
    protected FileEntryController(FileEntryProvider provider, SeaweedClient seaweedClient,
                                  IAuthenticatedUserProvider authenticatedUserProvider, FileEntryProvider fileEntryProvider,
                                  FileEntryConverter converter, SeaweedConfigurator seaweedConfigurator, ThumbnailController thumbnailController) {
        super(provider, converter);
        this.seaweedClient = seaweedClient;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.fileEntryProvider = fileEntryProvider;
        this.seaweedConfigurator = seaweedConfigurator;
        this.thumbnailController = thumbnailController;
    }

    @Override
    protected FileEntryConverterRequest createConverterRequest(FileEntry fileEntry) {
        return new FileEntryConverterRequest(fileEntry);
    }

    @Override
    public FileEntryDTO create(FileEntryDTO dto, String creatorName) {
        if (dto.getCreatedBy() == null && creatorName != null) {
            dto.setCreatedBy(creatorName);
        }
        validate(dto);
        final FileEntryDTO dtoStored = convert(getProvider().save(getConverter().reverse(dto)));
        DtoControllerLogger.info(this.getClass(), "Entity '{}' created by '{}'.", dtoStored, creatorName);
        return dtoStored;
    }

    @Override
    public Collection<FileEntryDTO> create(Collection<FileEntryDTO> fileEntryDTOS, String creatorName) {
        final List<FileEntryDTO> results = new ArrayList<>();
        fileEntryDTOS.forEach(fileEntryDTO -> create(fileEntryDTO, creatorName));
        return results;
    }


    public Resource downloadAsResource(UUID uuid, boolean checkIfPublic, String username) {
        final FileEntry fileEntry =
                getProvider().get(uuid).orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'."));

        if (checkIfPublic && !fileEntry.isPublic()) {
            KnowledgeSystemLogger.warning(this.getClass(), "Trying to access to file '{}' using the public api. FileEntry is private!", uuid);
            //Same error as before.
            throw new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'.");
        }

        KnowledgeSystemLogger.debug(this.getClass(), "User '{}' is downloading file '{}'.", username, uuid);

        return downloadAsResource(fileEntry);
    }

    public Resource downloadAsResource(String filePath) {
        final FileEntry fileEntry = getProvider().findByFilePath(filePath)
                .orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with path '" + filePath + "'."));
        return downloadAsResource(fileEntry);
    }

    public ChunkData downloadChunk(UUID uuid, long skip, int size, boolean checkIfPublic) {
        final FileEntry fileEntry = getProvider().get(uuid).orElseThrow(
                () -> new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'."));

        return downloadChunk(fileEntry, skip, size, checkIfPublic);
    }

    private ChunkData downloadChunk(FileEntry fileEntry, long skip, int size, boolean checkIfPublic) {
        return downloadChunk(fileEntry.getCompleteFilePath(), skip, size, checkIfPublic);
    }

    public ChunkData downloadChunk(String filePath, long skip, int size, boolean checkIfPublic) {
        final FileEntry fileEntry = getProvider().findByFilePath(filePath)
                .orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with path '" + filePath + "'."));

        if (checkIfPublic && !fileEntry.isPublic()) {
            KnowledgeSystemLogger.warning(this.getClass(), "Trying to access to file '{}' using the public api. FileEntry is private!", filePath);
            //Same error as before.
            throw new FileNotFoundException(this.getClass(), "No file with path '" + filePath + "'.");
        }

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

    public FileEntryDTO upload(MultipartFile file, FileEntryDTO fileEntryDTO, Boolean forceRewrite, String createdBy) {
        if (fileEntryDTO == null) {
            fileEntryDTO = new FileEntryDTO();
        }
        final FileEntry fileEntry = reverse(fileEntryDTO);
        setFields(fileEntry, file, createdBy);
        if (forceRewrite == null || !forceRewrite) {
            //Check if the file already exists.
            checkExistingFile(fileEntry);
        }
        try {
            if (!fileEntry.getFilePath().startsWith(seaweedConfigurator.getUploadsPath())) {
                throw new SeaweedClientException(this.getClass(), "Invalid file path '" + fileEntry.getFilePath()
                        + "'. Must be stored on '" + seaweedConfigurator.getUploadsPath() + "'");
            }
            seaweedClient.addFile(fileEntry.getFullPath(), file);
            //Save it on Opensearch
            fileEntryProvider.save(fileEntry);
            //Thumbnail generation.
            new Thread(() -> {
                updateThumbnail(fileEntry);
            }).start();
            return convert(fileEntry);
        } catch (IOException e) {
            throw new FileHandlingException(this.getClass(), e);
        } catch (OpenSearchException e) {
            //Cannot be stored on OpenSearch. Remove it from seaweed.
            seaweedClient.removeFile(fileEntry.getFullPath());
            throw new FileHandlingException(this.getClass(), e);
        }
    }

    private void updateThumbnail(FileEntry fileEntry) {
        try {
            thumbnailController.setThumbnail(fileEntry);
        } catch (IOException e) {
            KnowledgeSystemLogger.errorMessage(this.getClass(), e);
            fileEntry.setThumbnailUrl(null);
        }
        fileEntryProvider.save(fileEntry);
    }


    private void setFields(FileEntry fileEntry, MultipartFile file, String createdBy) {
        fileEntry.setMimeType(MediaTypeCalculator.getRealMimeType(file));
        fileEntry.setFileName(file.getOriginalFilename());
        if (fileEntry.getUuid() == null) {
            fileEntry.setUuid(UUID.randomUUID());
        }
        if (fileEntry.getCreatedAt() == null) {
            fileEntry.setCreatedAt(LocalDateTime.now());
        }
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(createdBy).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No User with username '" + createdBy + "' found on the system."));
        fileEntry.setCreatedBy(user.getUID());
        fileEntry.setFilePath(seaweedConfigurator.getUploadsPath() + File.separator + user.getUID());
    }


    private void checkExistingFile(FileEntry fileEntry) {
        if (seaweedClient.getEntry(fileEntry.getFilePath(), fileEntry.getFileName()) != null) {
            throw new FileAlreadyExistsException(this.getClass(), "File '" + fileEntry + "' already exists.");
        }
    }

    @Scheduled(cron = "@midnight")
    public void updateThumbnails() {
        final List<FileEntry> fileEntries = fileEntryProvider.findFilesWithoutThumbnail();
        KnowledgeSystemLogger.info(this.getClass(), "Found '{}' files that have a missing thumbnail.", fileEntries.size());
        fileEntries.forEach(
                this::updateThumbnail
        );
    }
}
