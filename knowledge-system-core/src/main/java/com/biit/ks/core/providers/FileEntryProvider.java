package com.biit.ks.core.providers;


import com.biit.ks.core.exceptions.FileAlreadyExistsException;
import com.biit.ks.core.exceptions.FileHandlingException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.exceptions.SeaweedClientException;
import com.biit.ks.core.files.MediaTypeCalculator;
import com.biit.ks.core.seaweed.SeaweedClient;
import com.biit.ks.core.seaweed.SeaweedConfigurator;
import com.biit.ks.persistence.entities.FileEntry;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchException;
import com.biit.ks.persistence.repositories.FileEntryRepository;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileEntryProvider extends CategorizedElementProvider<FileEntry, FileEntryRepository> {

    private final SeaweedClient seaweedClient;
    private final SeaweedConfigurator seaweedConfigurator;
    private final IAuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    public FileEntryProvider(FileEntryRepository fileEntryRepository,
                             SeaweedClient seaweedClient, SeaweedConfigurator seaweedConfigurator,
                             IAuthenticatedUserProvider authenticatedUserProvider) {
        super(fileEntryRepository);
        this.seaweedClient = seaweedClient;
        this.seaweedConfigurator = seaweedConfigurator;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public FileEntry save(MultipartFile file, FileEntry fileEntry, Boolean forceRewrite, String createdBy) {
        if (fileEntry == null) {
            fileEntry = new FileEntry();
        }
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
            return save(fileEntry);
        } catch (IOException e) {
            throw new FileHandlingException(this.getClass(), e);
        } catch (OpenSearchException e) {
            //Cannot be stored on OpenSearch. Remove it from seaweed.
            seaweedClient.removeFile(fileEntry.getFullPath());
            throw new FileHandlingException(this.getClass(), e);
        }
    }

    @Override
    public void delete(FileEntry element) {
        if (element.getFilePath() != null && !element.getFullPath().isBlank()) {
            seaweedClient.removeFile(element.getFullPath());
        }
        if (element.getUuid() != null) {
            super.delete(element);
        } else {
            getRepository().deleteByAlias(element.getAlias());
        }
    }


    @Override
    public void delete(UUID uuid) {
        final FileEntry fileEntry = get(uuid).orElseThrow(() -> new FileNotFoundException(this.getClass(), "No file with uuid '" + uuid + "'."));
        delete(fileEntry);
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


    public Optional<FileEntry> findByFilePath(String filePath) {
        if (filePath == null) {
            return Optional.empty();
        }
        final File f = new File(filePath);
        final String realFilePath = f.getParent();
        final String fileName = f.getName();
        return getRepository().findFileEntryByFilePathAndFileName(realFilePath, fileName);
    }


    public List<FileEntry> findFilesWithoutThumbnail() {
        return getRepository().findFileEntriesWithThumbnailIsNull();
    }


    public List<FileEntry> findByAlias(String alias, Integer from, Integer size) {
        if (alias == null) {
            return new ArrayList<>();
        }
        return getRepository().findFileEntryByAlias(alias, from, size);
    }

    public long countFileEntryByAlias(String alias) {
        if (alias == null) {
            return 0;
        }
        return getRepository().countFileEntryByAlias(alias);
    }
}
