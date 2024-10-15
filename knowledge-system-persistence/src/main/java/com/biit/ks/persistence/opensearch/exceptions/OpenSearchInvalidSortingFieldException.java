package com.biit.ks.persistence.opensearch.exceptions;


import com.biit.logger.ExceptionType;

import java.io.Serial;

public class OpenSearchInvalidSortingFieldException extends OpenSearchException {

    @Serial
    private static final long serialVersionUID = -6087065092588029424L;

    public OpenSearchInvalidSortingFieldException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public OpenSearchInvalidSortingFieldException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE);
    }

    public OpenSearchInvalidSortingFieldException(Class<?> clazz) {
        this(clazz, "Form not found");
    }

    public OpenSearchInvalidSortingFieldException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
