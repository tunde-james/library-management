package com.example.librarymanagement.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger((GlobalExceptionHandler.class));

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
        MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex
            .getBindingResult()
            .getFieldErrors()
            .forEach(error -> errors
                .put(error.getField(), error.getDefaultMessage()));

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

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(
        Exception ex) {

        log.warn("Email address already exist {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Email address already exists ");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUsernameAlreadyExistsException(
        Exception ex) {

        log.warn("Username already exist {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Username already exists ");

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(
        BadCredentialsException ex) {

        log.warn("Invalid login attemp {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Invalid username or password");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }
}
