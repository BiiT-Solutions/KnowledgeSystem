package com.biit.ks.client.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;


public class InvalidConfigurationException extends LoggedException {
    private static final long serialVersionUID = 7132994133678894370L;

    public InvalidConfigurationException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING, null);
    }
}
