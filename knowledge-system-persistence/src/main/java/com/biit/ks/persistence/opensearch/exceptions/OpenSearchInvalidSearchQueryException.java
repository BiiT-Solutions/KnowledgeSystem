package com.biit.ks.persistence.opensearch.exceptions;


import com.biit.logger.ExceptionType;

import java.io.Serial;

public class OpenSearchInvalidSearchQueryException extends OpenSearchException {

    @Serial
    private static final long serialVersionUID = -6907781623018383835L;

    public OpenSearchInvalidSearchQueryException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public OpenSearchInvalidSearchQueryException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE);
    }

    public OpenSearchInvalidSearchQueryException(Class<?> clazz) {
        this(clazz, "Form not found");
    }

    public OpenSearchInvalidSearchQueryException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
