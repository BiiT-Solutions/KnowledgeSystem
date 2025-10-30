package com.biit.ks.persistence.opensearch.exceptions;

/*-
 * #%L
 * Knowledge System (Persistence)
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

public class OpenSearchConnectionException extends OpenSearchException {

    public OpenSearchConnectionException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public OpenSearchConnectionException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.WARNING);
    }

    public OpenSearchConnectionException(Class<?> clazz) {
        this(clazz, "Form not found");
    }

    public OpenSearchConnectionException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
