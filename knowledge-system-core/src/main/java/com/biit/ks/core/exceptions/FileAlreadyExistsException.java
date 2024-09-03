package com.biit.ks.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileAlreadyExistsException extends LoggedException {

    public FileAlreadyExistsException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public FileAlreadyExistsException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.NOT_FOUND);
    }

    public FileAlreadyExistsException(Class<?> clazz) {
        this(clazz, "File already exists");
    }

    public FileAlreadyExistsException(Class<?> clazz, String message, Throwable e) {
        super(clazz, message, e);
    }

    public FileAlreadyExistsException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
