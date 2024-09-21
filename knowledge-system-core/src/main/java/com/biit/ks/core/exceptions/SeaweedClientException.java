package com.biit.ks.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class SeaweedClientException extends LoggedException {

    @Serial
    private static final long serialVersionUID = -6199332968769763797L;

    public SeaweedClientException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public SeaweedClientException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.NOT_FOUND);
    }

    public SeaweedClientException(Class<?> clazz) {
        this(clazz, "Element cannot be stored");
    }

    public SeaweedClientException(Class<?> clazz, String message, Throwable e) {
        super(clazz, message, e);
    }

    public SeaweedClientException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
