package com.biit.ks.persistence.entities;

import com.biit.database.encryption.ByteArrayCryptoConverter;
import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.context.annotation.Primary;

@Entity
@Primary
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "files")
public class File extends Element {
    // 2mb
    private static final int MAX_FILE_SIZE = 2 * 1024 * 1024;

    @Lob
    @Column(name = "data", length = MAX_FILE_SIZE, nullable = false)
    @Convert(converter = ByteArrayCryptoConverter.class)
    private byte[] data;

    @Column(name = "file-format", nullable = false)
    @Convert(converter = StringCryptoConverter.class)
    private String fileFormat;
}
