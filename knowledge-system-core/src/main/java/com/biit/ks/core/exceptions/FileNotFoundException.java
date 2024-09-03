package com.biit.ks.core.exceptions;

import com.biit.logger.ExceptionType;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public FileNotFoundException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public FileNotFoundException(Class<?> clazz) {
        this(clazz, "File not found");
    }

    public FileNotFoundException(Class<?> clazz, String message, Throwable e) {
        super(clazz, e);
    }

    public FileNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
