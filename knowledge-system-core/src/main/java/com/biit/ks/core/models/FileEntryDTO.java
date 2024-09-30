package com.biit.ks.core.models;

import com.biit.server.controllers.models.ElementDTO;

import java.io.Serial;
import java.util.UUID;

public class FileEntryDTO extends ElementDTO<UUID> {
    @Serial
    private static final long serialVersionUID = 3144187148832541249L;

    private UUID uuid;
    private String filePath;
    private String fileName;
    private String fileFormat;
    private String mimeType;
    private String description;
    private boolean isPublic = false;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getPublicUrl() {
        if (!isPublic()) {
            return null;
        }
        return "/files/public/download/" + uuid.toString();
    }

    @Override
    public String toString() {
        return "FileEntryDTO{"
                + "filePath='" + filePath + '\''
                + ", fileName='" + fileName + '\''
                + '}';
    }
}
