package com.biit.ks.core.solr.models;

import org.apache.solr.client.solrj.beans.Field;

public class SolrDocument {
    @Field
    private String id;
    @Field
    private String name;

    public SolrDocument(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SolrDocument() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
