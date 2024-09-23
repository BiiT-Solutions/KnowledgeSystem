package com.biit.ks.persistence.opensearch.exceptions;


import com.biit.logger.ExceptionType;

import java.io.Serial;

public class OpenSearchIndexMissingException extends OpenSearchException {

    @Serial
    private static final long serialVersionUID = 8642760371825324848L;

    public OpenSearchIndexMissingException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public OpenSearchIndexMissingException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public OpenSearchIndexMissingException(Class<?> clazz) {
        this(clazz, "Form not found");
    }

    public OpenSearchIndexMissingException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
