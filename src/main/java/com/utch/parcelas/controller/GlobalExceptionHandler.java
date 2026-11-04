package com.utch.parcelas.controller;

import com.utch.parcelas.exception.DuplicateParcelException;
import com.utch.parcelas.exception.ParcelNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(int status, String code, String message) {
    }

    @ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ParcelNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "PARCEL_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(DuplicateParcelException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateParcelException ex) {
        return build(HttpStatus.CONFLICT, "PARCEL_DUPLICATE", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), code, message));
    }
}
