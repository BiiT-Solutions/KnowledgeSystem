package com.biit.ks.core.exceptions;


import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends LoggedException {

    public NotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(Class<?> clazz) {
        this(clazz, "Object not found");
    }

    public NotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
