package com.biit.ks.core.opensearch.search;

public class Range {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private String parameter;
    // gt - lt
    private Object lt;
    private Object lte;
    private Object gt;
    private Object gte;

    public Range() {
    }

    public Range(String parameter, Object gt, Object lt) {
        this();
        this.parameter = parameter;
        this.gt = gt;
        this.lt = lt;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Object getLt() {
        return lt;
    }

    public void setLt(Object lt) {
        this.lt = lt;
    }

    public Object getLte() {
        return lte;
    }

    public void setLte(Object lte) {
        this.lte = lte;
    }

    public Object getGt() {
        return gt;
    }

    public void setGt(Object gt) {
        this.gt = gt;
    }

    public Object getGte() {
        return gte;
    }

    public void setGte(Object gte) {
        this.gte = gte;
    }
}
