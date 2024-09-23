package com.biit.ks.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileHandlingException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 2253428972799761588L;

    public FileHandlingException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public FileHandlingException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.NOT_FOUND);
    }

    public FileHandlingException(Class<?> clazz) {
        this(clazz, "Error handling files!");
    }

    public FileHandlingException(Class<?> clazz, String message, Throwable e) {
        super(clazz, message, e);
    }

    public FileHandlingException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
