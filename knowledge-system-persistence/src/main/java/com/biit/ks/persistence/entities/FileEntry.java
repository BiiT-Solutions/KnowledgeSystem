package com.biit.ks.persistence.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.util.UUID;


public class FileEntry extends Element<UUID> {


    private UUID uuid;


    private String filePath;


    private String fileName;


    private String fileFormat;


    private String mimeType;

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
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @JsonIgnore
    public String getCompleteFilePath() {
        if (filePath != null) {
            return filePath + File.separator + fileName;
        }
        return fileName;
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
            return filePath + File.separator + fileName;
        }
        return fileName;
    }

    @Override
    public String toString() {
        return "FileEntry{"
                + "uuid=" + uuid
                + ", filePath='" + filePath + '\''
                + ", fileName='" + fileName + '\''
                + '}';
    }
}
