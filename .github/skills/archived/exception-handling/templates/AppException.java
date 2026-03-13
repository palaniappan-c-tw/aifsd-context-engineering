package com.example.exception;

/**
 * Base exception for all application-level runtime exceptions.
 * Never throw AppException directly — use or create a specific subtype.
 *
 * Built-in subtypes:
 *   ResourceNotFoundException     → HTTP 404
 *   BusinessRuleViolationException → HTTP 422
 *   ConflictException              → HTTP 409
 *   ExternalServiceException       → HTTP 502
 */
public abstract class AppException extends RuntimeException {

    private final String code;

    protected AppException(String code, String message) {
        super(message);
        this.code = code;
    }

    protected AppException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

// ---------------------------------------------------------------------------
// Subtype: ResourceNotFoundException  →  HTTP 404
// ---------------------------------------------------------------------------
class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String code, String message) {
        super(code, message);
    }
}

// ---------------------------------------------------------------------------
// Subtype: BusinessRuleViolationException  →  HTTP 422
// ---------------------------------------------------------------------------
class BusinessRuleViolationException extends AppException {

    public BusinessRuleViolationException(String code, String message) {
        super(code, message);
    }
}

// ---------------------------------------------------------------------------
// Subtype: ConflictException  →  HTTP 409
// ---------------------------------------------------------------------------
class ConflictException extends AppException {

    public ConflictException(String code, String message) {
        super(code, message);
    }
}

// ---------------------------------------------------------------------------
// Subtype: ExternalServiceException  →  HTTP 502
// Always pass the original cause so the stack trace is preserved.
// ---------------------------------------------------------------------------
class ExternalServiceException extends AppException {

    public ExternalServiceException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
