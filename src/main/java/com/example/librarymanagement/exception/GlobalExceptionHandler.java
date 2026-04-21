package com.example.librarymanagement.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger((GlobalExceptionHandler.class));

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
        MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
            error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFoundException(
        BookNotFoundException ex) {

        log.warn("Book not found {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Book not found");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(
        UserNotFoundException ex) {

        log.warn("User not found {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "User not found");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<Map<String, String>> handleBookNotAvailableException(
        BookNotAvailableException ex) {

        log.warn("Book is not available {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Book is not available");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLoanNotFoundException(
        Exception ex) {

        log.warn("Loan not found with {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Loan not found");

        return ResponseEntity.badRequest().body(errors);
    }
}
