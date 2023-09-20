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
@Table(name = "form_rules")
public class FormRules extends Element {

    private static final int MAX_JSON_LENGTH = 100000;

    @Column(name = "name")
    private String name;

    @Column(name = "version")
    private Integer version;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "rules", length = MAX_JSON_LENGTH)
    private String rules;

    @Column(name = "metadata", length = MAX_JSON_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String metadata;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getRules() {
        return this.rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

}
