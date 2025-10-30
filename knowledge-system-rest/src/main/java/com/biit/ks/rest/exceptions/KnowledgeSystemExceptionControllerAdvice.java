package com.biit.ks.rest.exceptions;

/*-
 * #%L
 * Knowledge System (Rest)
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

import com.biit.kafka.exceptions.InvalidEventException;
import com.biit.ks.core.exceptions.CategoryAlreadyExistsException;
import com.biit.ks.core.exceptions.CategoryNotFoundException;
import com.biit.ks.core.exceptions.FileAlreadyExistsException;
import com.biit.ks.core.exceptions.FileHandlingException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.exceptions.SeaweedClientException;
import com.biit.ks.core.exceptions.TextAlreadyExistsException;
import com.biit.ks.logger.KnowledgeSystemLogger;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchException;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchInvalidSearchQueryException;
import com.biit.server.exceptions.ErrorResponse;
import com.biit.server.exceptions.NotFoundException;
import com.biit.server.exceptions.ServerExceptionControllerAdvice;
import com.biit.usermanager.client.exceptions.ElementNotFoundException;
import com.biit.usermanager.client.exceptions.InvalidConfigurationException;
import com.biit.usermanager.client.exceptions.InvalidValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class KnowledgeSystemExceptionControllerAdvice extends ServerExceptionControllerAdvice {


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "not_found", ex), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(SeaweedClientException.class)
    public ResponseEntity<Object> seaweedClientException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "error_storing_file", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OpenSearchException.class)
    public ResponseEntity<Object> openSearchException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "open_search_exception", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileHandlingException.class)
    public ResponseEntity<Object> fileHandlingException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "error_handling_files", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> fileNotFoundException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "file_not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OpenSearchInvalidSearchQueryException.class)
    public ResponseEntity<Object> openSearchInvalidSearchQueryException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_query", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<Object> fileAlreadyExistsException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "file_already_exists", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<Object> categoryAlreadyExistsException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "category_already_exists", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Object> categoryNotFoundException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "category_not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TextAlreadyExistsException.class)
    public ResponseEntity<Object> textAlreadyExistsException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "text_already_exists_with_this_name", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidEventException.class)
    public ResponseEntity<Object> invalidEventException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "cannot_connect_to_kafka", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<Object> elementNotFoundException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidConfigurationException.class)
    public ResponseEntity<Object> invalidConfigurationException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_configuration_exception", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<Object> invalidValueException(Exception ex) {
        KnowledgeSystemLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_parameter", ex), HttpStatus.BAD_REQUEST);
    }

}
