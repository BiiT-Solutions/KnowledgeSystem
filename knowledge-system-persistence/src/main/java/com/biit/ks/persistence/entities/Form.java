package com.biit.ks.persistence.entities;

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.context.annotation.Primary;



@Entity
@Primary
@Table(name = "forms")
public class Form extends Element {
    private static final int MAX_JSON_LENGTH = 100000;

    @Column(name = "name")
    @Convert(converter = StringCryptoConverter.class)
    private String name;

    @Column(name = "version")
    private Integer version;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "value", length = MAX_JSON_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String value;

    @Column(name = "description")
    private String description;


    public Form() {
        super();
    }

    public String getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return this.version;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

}
