package com.biit.ks.logger;

/*-
 * #%L
 * Knowledge System (Logger)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.logger.ExceptionType;
import org.springframework.http.HttpStatus;

public class LoggedException extends RuntimeException {
    private HttpStatus status;

    public LoggedException(Class<?> clazz, String message, ExceptionType type) {
        this(clazz, message, type, null);
    }

    public LoggedException(Class<?> clazz, String message, Throwable e, ExceptionType type, HttpStatus status) {
        this(clazz, message, type, status);
        KnowledgeSystemLogger.errorMessage(clazz, e);
    }

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
