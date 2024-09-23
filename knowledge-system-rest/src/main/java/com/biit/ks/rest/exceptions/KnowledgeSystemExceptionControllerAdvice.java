package com.biit.ks.rest.exceptions;

import com.biit.ks.core.exceptions.FileHandlingException;
import com.biit.ks.core.exceptions.FileNotFoundException;
import com.biit.ks.core.exceptions.SeaweedClientException;
import com.biit.ks.persistence.opensearch.exceptions.OpenSearchException;
import com.biit.server.exceptions.ErrorResponse;
import com.biit.server.exceptions.NotFoundException;
import com.biit.server.exceptions.ServerExceptionControllerAdvice;
import com.biit.server.logger.RestServerExceptionLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class KnowledgeSystemExceptionControllerAdvice extends ServerExceptionControllerAdvice {


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", "not_found", ex), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(SeaweedClientException.class)
    public ResponseEntity<Object> seaweedClientException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", "error_storing_file", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OpenSearchException.class)
    public ResponseEntity<Object> openSearchException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("INTERNAL_SERVER_ERROR", "open_search_exception", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileHandlingException.class)
    public ResponseEntity<Object> fileHandlingException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("INTERNAL_SERVER_ERROR", "error_handling_files", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> fileNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", "file_not_found", ex), HttpStatus.NOT_FOUND);
    }
}
