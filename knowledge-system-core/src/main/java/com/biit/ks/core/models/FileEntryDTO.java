package com.biit.ks.core.models;

import java.io.Serial;
import java.util.UUID;

public class FileEntryDTO extends CategorizedElementDTO<UUID> {

    @Serial
    private static final long serialVersionUID = 3144187148832541249L;

    private UUID uuid;
    private String alias;
    private String filePath;
    private String fileName;
    private String fileFormat;
    private String mimeType;
    private String description;
    private boolean isPublic = false;
    private String thumbnailUrl;

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
        return "FileEntryDTO{"
                + "filePath='" + filePath + '\''
                + ", fileName='" + fileName + '\''
                + '}';
    }
}
