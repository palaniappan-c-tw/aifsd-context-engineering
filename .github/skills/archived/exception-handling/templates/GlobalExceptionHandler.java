package com.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Central exception handler. Maps every AppException subtype (and validation /
 * unexpected errors) to a consistent ErrorResponse.
 *
 * Do NOT add try-catch blocks in controllers — let all exceptions reach here.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // -----------------------------------------------------------------------
    // 404 — Resource not found
    // -----------------------------------------------------------------------
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found. path={}, code={}, message={}",
                request.getRequestURI(), ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, ex.getCode(), ex.getMessage(), request.getRequestURI()));
    }

    // -----------------------------------------------------------------------
    // 422 — Business rule violation
    // -----------------------------------------------------------------------
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(
            BusinessRuleViolationException ex, HttpServletRequest request) {
        log.warn("Business rule violated. path={}, code={}, message={}",
                request.getRequestURI(), ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(422, ex.getCode(), ex.getMessage(), request.getRequestURI()));
    }

    // -----------------------------------------------------------------------
    // 409 — Conflict
    // -----------------------------------------------------------------------
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex, HttpServletRequest request) {
        log.warn("Conflict. path={}, code={}, message={}",
                request.getRequestURI(), ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(409, ex.getCode(), ex.getMessage(), request.getRequestURI()));
    }

    // -----------------------------------------------------------------------
    // 502 — External service failure (always log stack trace)
    // -----------------------------------------------------------------------
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(
            ExternalServiceException ex, HttpServletRequest request) {
        log.error("External service failure. path={}, code={}, message={}",
                request.getRequestURI(), ex.getCode(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(502, ex.getCode(), ex.getMessage(), request.getRequestURI()));
    }

    // -----------------------------------------------------------------------
    // 400 — @Valid / @Validated constraint violations
    // -----------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validation failed. path={}, errors={}", request.getRequestURI(), message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "VALIDATION_FAILED", message, request.getRequestURI()));
    }

    // -----------------------------------------------------------------------
    // 500 — Catch-all (never expose internal details in the response)
    // -----------------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error. path={}", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "INTERNAL_ERROR",
                        "An unexpected error occurred", request.getRequestURI()));
    }
}
