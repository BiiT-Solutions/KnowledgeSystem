package com.biit.ks.persistence.opensearch.exceptions;

import com.biit.ks.logger.LoggedException;
import com.biit.logger.ExceptionType;

import java.io.Serial;

public class OpenSearchException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 4479155239577921897L;

    public OpenSearchException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public OpenSearchException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public OpenSearchException(Class<?> clazz) {
        this(clazz, "Error in OpenSearch");
    }

    public OpenSearchException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
