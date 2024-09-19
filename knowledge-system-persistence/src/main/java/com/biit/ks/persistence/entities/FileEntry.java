package com.biit.ks.persistence.entities;

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.util.UUID;

@Entity
@Primary
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "files", uniqueConstraints = {@UniqueConstraint(columnNames = {"file_path", "file_name"})})
public class FileEntry extends Element<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "file_path")
    @Convert(converter = StringCryptoConverter.class)
    private String filePath;

    @Column(name = "file_name", nullable = false)
    @Convert(converter = StringCryptoConverter.class)
    private String fileName;

    @Column(name = "file_format", nullable = false)
    @Convert(converter = StringCryptoConverter.class)
    private String fileFormat;

    private String openSearchID;

    private String openSearchIndex;

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

    @Override
    public String toString() {
        return "FileEntry{"
                + "uuid=" + uuid
                + ", filePath='" + filePath + '\''
                + ", fileName='" + fileName + '\''
                + '}';
    }
}
