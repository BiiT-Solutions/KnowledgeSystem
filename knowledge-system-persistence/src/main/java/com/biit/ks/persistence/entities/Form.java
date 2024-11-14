package com.biit.ks.persistence.entities;

public class Form extends CategorizedElement<Long> {
    private static final int MAX_JSON_LENGTH = 100000;


    private Long id;

    private Integer version;

    private String organizationId;

    private String value;


    public Form() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return this.organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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

}
