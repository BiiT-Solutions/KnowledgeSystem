package com.biit.ks.core.exceptions;


import com.biit.logger.ExceptionType;

public class FormNotFoundException extends NotFoundException {

    public FormNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public FormNotFoundException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public FormNotFoundException(Class<?> clazz) {
        this(clazz, "Form not found");
    }

    public FormNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
