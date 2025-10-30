package com.biit.ks.core.exceptions;

/*-
 * #%L
 * Knowledge System (Core)
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
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryAlreadyExistsException extends LoggedException {

    @Serial
    private static final long serialVersionUID = -5720147344844445298L;

    public CategoryAlreadyExistsException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.BAD_REQUEST);
    }

    public CategoryAlreadyExistsException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.BAD_REQUEST);
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
