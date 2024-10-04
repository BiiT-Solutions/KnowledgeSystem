package com.biit.ks.core.exceptions;

import com.biit.logger.ExceptionType;

import java.io.Serial;

public class CategoryNotFoundException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 8222810911313176207L;

    public CategoryNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public CategoryNotFoundException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public CategoryNotFoundException(Class<?> clazz) {
        this(clazz, "Category not found");
    }

    public CategoryNotFoundException(Class<?> clazz, String message, Throwable e) {
        super(clazz, e);
    }

    public CategoryNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
