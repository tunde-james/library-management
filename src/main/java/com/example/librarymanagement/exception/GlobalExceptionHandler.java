package com.example.librarymanagement.exception;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger((GlobalExceptionHandler.class));

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {

                Map<String, String> fieldErrors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> fieldErrors
                                .put(error.getField(), error.getDefaultMessage()));

                ErrorResponse errorResponse = ErrorResponse.ofValidation(
                                HttpStatus.BAD_REQUEST.value(), "Validation Failed",
                                "One or more fields are invalid", request.getRequestURI(),
                                fieldErrors);

                return ResponseEntity.badRequest().body(errorResponse);
        }

        @ExceptionHandler(BookNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleBookNotFoundException(BookNotFoundException ex,
                        HttpServletRequest request) {

                log.warn("Book not found {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(),
                                "Not Found", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex,
                        HttpServletRequest request) {

                log.warn("User not found {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(),
                                "Not Found", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        @ExceptionHandler(LoanNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleLoanNotFoundException(Exception ex,
                        HttpServletRequest request) {

                log.warn("Loan not found with {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.NOT_FOUND.value(),
                                "Not Found", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        @ExceptionHandler(BookNotAvailableException.class)
        public ResponseEntity<ErrorResponse> handleBookNotAvailableException(
                        BookNotAvailableException ex, HttpServletRequest request) {

                log.warn("Book is not available {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                                "Conflict", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(BookAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleBookAlreadyExistsException(Exception ex,
                        HttpServletRequest request) {

                log.warn("This book already exist {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                                "Conflict", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(Exception ex,
                        HttpServletRequest request) {

                log.warn("Email address already exist {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                                "Conflict", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(UsernameAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(Exception ex,
                        HttpServletRequest request) {

                log.warn("Username already exist {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                                "Conflict", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(
                        BadCredentialsException ex, HttpServletRequest request) {

                log.warn("Invalid login attempt, invalid username or password {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        @ExceptionHandler(LockedException.class)
        public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex,
                        HttpServletRequest request) {

                log.warn("Locked account login attempt: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(),
                                "Forbidden", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        @ExceptionHandler(DisabledException.class)
        public ResponseEntity<ErrorResponse> handleDisabledException(DisabledException ex,
                        HttpServletRequest request) {

                log.warn("Disabled account login attempt: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(),
                                "Forbidden", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
        public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
                        ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {

                log.warn("Optimistic locking conflict: {}", ex.getMessage());

                ErrorResponse errorResponse =
                                ErrorResponse.of(HttpStatus.CONFLICT.value(), "Conflict",
                                                "This record was modified by another user. "
                                                                + "Please refresh and try again.",
                                                request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex,
                        HttpServletRequest request) {

                log.warn("Illegal state: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(),
                                "Conflict", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, HttpServletRequest request) {

                log.warn("Invalid argument: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
                                "Bad Request", ex.getMessage(), request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }



}
