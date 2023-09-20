package com.biit.ks.core.exceptions;


import com.biit.logger.ExceptionType;

public class FormRulesNotFoundException extends NotFoundException {

    public FormRulesNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public FormRulesNotFoundException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public FormRulesNotFoundException(Class<?> clazz) {
        this(clazz, "Form Rules not found");
    }

    public FormRulesNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
