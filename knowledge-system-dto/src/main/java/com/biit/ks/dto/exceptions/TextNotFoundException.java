package com.biit.ks.dto.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class TextNotFoundException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 5697086119221201996L;

    public TextNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public TextNotFoundException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.DEBUG, HttpStatus.NOT_FOUND);
    }

    public TextNotFoundException(Class<?> clazz) {
        this(clazz, "Element does not exists!");
    }

    public TextNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
