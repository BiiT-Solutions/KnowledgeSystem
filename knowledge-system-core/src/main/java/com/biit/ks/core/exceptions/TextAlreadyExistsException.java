package com.biit.ks.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TextAlreadyExistsException extends LoggedException {

    @Serial
    private static final long serialVersionUID = -5720147344844445298L;

    public TextAlreadyExistsException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.BAD_REQUEST);
    }

    public TextAlreadyExistsException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.BAD_REQUEST);
    }

    public TextAlreadyExistsException(Class<?> clazz) {
        this(clazz, "Category already exists");
    }

    public TextAlreadyExistsException(Class<?> clazz, String message, Throwable e) {
        super(clazz, message, e);
    }

    public TextAlreadyExistsException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
