package com.biit.ks.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryAlreadyExistsException extends LoggedException {

    @Serial
    private static final long serialVersionUID = -5720147344844445298L;

    public CategoryAlreadyExistsException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public CategoryAlreadyExistsException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.NOT_FOUND);
    }

    public CategoryAlreadyExistsException(Class<?> clazz) {
        this(clazz, "Category already exists");
    }

    public CategoryAlreadyExistsException(Class<?> clazz, String message, Throwable e) {
        super(clazz, message, e);
    }

    public CategoryAlreadyExistsException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
