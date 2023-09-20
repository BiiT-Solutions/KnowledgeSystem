package com.biit.ks.logger;


import com.biit.logger.ExceptionType;
import org.springframework.http.HttpStatus;

public class LoggedException extends RuntimeException {
    private HttpStatus status;

    protected LoggedException(Class<?> clazz, String message, ExceptionType type, HttpStatus status) {
        super(message);
        this.status = status;
        final String className = clazz.getName();
        switch (type) {
            case INFO:
               KnowledgeSystemLogger.info(className, message);
                break;
            case WARNING:
                KnowledgeSystemLogger.warning(className, message);
                break;
            case SEVERE:
                KnowledgeSystemLogger.severe(className, message);
                break;
            default:
                KnowledgeSystemLogger.debug(className, message);
                break;
        }
    }

    protected LoggedException(Class<?> clazz, Throwable e, HttpStatus status) {
        this(clazz, e);
        this.status = status;
    }

    protected LoggedException(Class<?> clazz, Throwable e) {
        super(e);
        KnowledgeSystemLogger.errorMessage(clazz, e);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
