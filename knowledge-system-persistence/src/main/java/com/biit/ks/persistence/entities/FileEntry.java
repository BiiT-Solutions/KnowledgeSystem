package com.biit.ks.persistence.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileEntry extends CategorizedElement<UUID> {

    private UUID uuid;
    private String alias;
    private String filePath;
    private String fileFormat;
    private String mimeType;
    private String thumbnailUrl;

    public FileEntry() {
        super();
        setUuid(UUID.randomUUID());
    }

    @Override
    public UUID getId() {
        return uuid;
    }

    @Override
    public void setId(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFileName() {
        return getName();
    }

    public void setFileName(String fileName) {
        setName(fileName);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @JsonIgnore
    public String getFullPath() {
        if (filePath != null) {
            return filePath + File.separator + getFileName();
        }
        return getFileName();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return "FileEntry{"
                + "uuid=" + uuid
                + ", filePath='" + filePath + '\''
                + ", fileName='" + getFileName() + '\''
                + '}';
    }
}
