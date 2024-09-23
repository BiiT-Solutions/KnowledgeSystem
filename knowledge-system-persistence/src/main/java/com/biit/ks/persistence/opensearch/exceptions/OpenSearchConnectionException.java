package com.biit.ks.persistence.opensearch.exceptions;


import com.biit.logger.ExceptionType;

public class OpenSearchConnectionException extends OpenSearchException {

    public OpenSearchConnectionException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public OpenSearchConnectionException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public OpenSearchConnectionException(Class<?> clazz) {
        this(clazz, "Form not found");
    }

    public OpenSearchConnectionException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
