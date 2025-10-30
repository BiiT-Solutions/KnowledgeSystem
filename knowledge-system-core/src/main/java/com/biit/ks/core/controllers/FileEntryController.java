package com.biit.ks.core.controllers;

/*-
 * #%L
 * Knowledge System (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.ks.core.converters.FileEntryConverter;
import com.biit.ks.core.converters.models.FileEntryConverterRequest;
import com.biit.ks.core.exceptions.FileAlreadyExistsException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.files.MediaTypeCalculator;
import com.biit.ks.core.models.ChunkData;
import com.biit.ks.core.providers.FileEntryProvider;
import com.biit.ks.core.providers.ThumbnailProvider;
import com.biit.ks.core.seaweed.SeaweedClient;
import com.biit.ks.core.seaweed.SeaweedConfigurator;
import com.biit.ks.dto.FileEntryDTO;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.search.SearchWrapper;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.logger.DtoControllerLogger;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.model.IAuthenticatedUser;
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

    private static final int SIZE = 100;

    private final SeaweedClient seaweedClient;
    private final IAuthenticatedUserProvider<?> authenticatedUserProvider;
    private final FileEntryProvider fileEntryProvider;
    private final SeaweedConfigurator seaweedConfigurator;
    private final ThumbnailProvider thumbnailProvider;


    @Autowired
    protected FileEntryController(FileEntryProvider provider, SeaweedClient seaweedClient,
                                  IAuthenticatedUserProvider<?> authenticatedUserProvider, FileEntryProvider fileEntryProvider,
                                  FileEntryConverter converter, SeaweedConfigurator seaweedConfigurator, ThumbnailProvider thumbnailProvider) {
        super(provider, converter);
        this.seaweedClient = seaweedClient;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.fileEntryProvider = fileEntryProvider;
        this.seaweedConfigurator = seaweedConfigurator;
        this.thumbnailProvider = thumbnailProvider;
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
        fileEntryDTOS.forEach(fileEntryDTO -> results.add(create(fileEntryDTO, creatorName)));
        return results;
    }


    public Resource downloadAsResource(UUID uuid, boolean checkIfPublic, String username) {
        final SearchWrapper<FileEntry> fileEntry = getProvider().get(uuid);

        if (checkIfPublic && !fileEntry.getFirst().isPublic()) {
            KnowledgeSystemLogger.warning(this.getClass(), "Trying to access to file '{}' using the public api. FileEntry is private!", uuid);
            //Same error as before.
            throw new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'.");
        }

        KnowledgeSystemLogger.debug(this.getClass(), "User '{}' is downloading file '{}'.", username, uuid);

        return downloadAsResource(fileEntry.getFirst());
    }


    public Resource downloadAsResource(String filePath) {
        final FileEntry fileEntry = getProvider().findByFilePath(filePath)
                .orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with path '" + filePath + "'."));
        return downloadAsResource(fileEntry);
    }


    public ChunkData downloadChunk(UUID uuid, long skip, int size, boolean checkIfPublic) {
        final SearchWrapper<FileEntry> fileEntry = getProvider().get(uuid);
        return downloadChunk(fileEntry.getFirst(), skip, size, checkIfPublic);
    }


    private ChunkData downloadChunk(FileEntry fileEntry, long skip, int size, boolean checkIfPublic) {
        return downloadChunk(fileEntry.getFullPath(), skip, size, checkIfPublic);
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
        final FileEntry savedFileEntry = fileEntryProvider.save(file, fileEntry, forceRewrite, createdBy);
        //Thumbnail generation.
        new Thread(() -> {
            updateThumbnail(savedFileEntry);
        }).start();
        return convert(savedFileEntry);
    }


    private void updateThumbnail(FileEntry fileEntry) {
        try {
            thumbnailProvider.setThumbnail(fileEntry);
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
        final SearchWrapper<FileEntry> fileEntries = fileEntryProvider.findFilesWithoutThumbnail();
        KnowledgeSystemLogger.info(this.getClass(), "Found '{}' files that have a missing thumbnail.", fileEntries.getTotalElements());
        fileEntries.getData().forEach(this::updateThumbnail);
    }

    public void updateAllThumbnails() {
        int loop = 0;
        SearchWrapper<FileEntry> fileEntries = fileEntryProvider.getAll(0, SIZE);
        while (!fileEntries.getData().isEmpty()) {
            KnowledgeSystemLogger.info(this.getClass(), "Regenerating thumbnail for '{}' files.", fileEntries.getData().size());
            fileEntries.getData().forEach(this::updateThumbnail);
            loop++;
            fileEntries = fileEntryProvider.getAll(loop * SIZE, SIZE);
        }
    }


    public int deleteByAlias(String alias, String deleteBy) {
        KnowledgeSystemLogger.warning(this.getClass(), "User '{}' deletes files with alias '{}'.", deleteBy, alias);
        KnowledgeSystemLogger.warning(this.getClass(), "Files to be deleted are '{}'.", fileEntryProvider.countFileEntryByAlias(alias));
        SearchWrapper<FileEntry> fileEntries = fileEntryProvider.findByAlias(alias, 0, SIZE);
        int counter = fileEntries.getData().size();
        while (!fileEntries.isEmpty()) {
            fileEntries.getData().forEach(fileEntryProvider::delete);
            //As are deleted, no need to increase starting point.
            fileEntries = fileEntryProvider.findByAlias(alias, 0, SIZE);
            counter += fileEntries.getData().size();
        }
        return counter;
    }
}
