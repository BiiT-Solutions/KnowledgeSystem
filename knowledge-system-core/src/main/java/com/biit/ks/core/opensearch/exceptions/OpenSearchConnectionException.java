package com.biit.ks.core.opensearch.exceptions;


import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;

public class OpenSearchConnectionException extends LoggedException {

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
