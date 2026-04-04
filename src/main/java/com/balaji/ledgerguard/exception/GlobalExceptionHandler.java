package com.balaji.ledgerguard.exception;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "One or more fields failed validation"
        );
        problemDetail.setType(java.net.URI.create("https://ledgerguard.example/validation-error"));
        problemDetail.setTitle("Validation failed");

        List<Map<String, String>> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage()
                ))
                .toList();
        problemDetail.setProperty("fieldErrors", fieldErrors);
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setType(java.net.URI.create("https://ledgerguard.example/not-found"));
        problemDetail.setTitle("Resource not found");
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbiddenOperation(ForbiddenOperationException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
        problemDetail.setType(java.net.URI.create("https://ledgerguard.example/forbidden"));
        problemDetail.setTitle("Forbidden");
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "One or more fields failed validation"
        );
        problemDetail.setType(java.net.URI.create("https://ledgerguard.example/validation-error"));
        problemDetail.setTitle("Validation failed");

        List<Map<String, String>> fieldErrors = exception.getConstraintViolations()
                .stream()
                .map(violation -> Map.of(
                        "field", violation.getPropertyPath().toString(),
                        "message", violation.getMessage()
                ))
                .toList();
        problemDetail.setProperty("fieldErrors", fieldErrors);
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }

        @ExceptionHandler(InvalidOperationException.class)
        public ProblemDetail handleInvalidOperation(InvalidOperationException exception) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
                problemDetail.setType(java.net.URI.create("https://ledgerguard.example/invalid-operation"));
                problemDetail.setTitle("Invalid operation");
                problemDetail.setProperty("timestamp", Instant.now().toString());
                return problemDetail;
        }

        @ExceptionHandler(DuplicateEmailException.class)
        public ProblemDetail handleDuplicateEmail(DuplicateEmailException exception) {
                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
                problemDetail.setType(java.net.URI.create("https://ledgerguard.example/conflict"));
                problemDetail.setTitle("Conflict");
                problemDetail.setProperty("timestamp", Instant.now().toString());
                return problemDetail;
        }
}
