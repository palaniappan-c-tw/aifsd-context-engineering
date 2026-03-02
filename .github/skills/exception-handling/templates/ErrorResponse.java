package com.example.exception;

import java.time.Instant;

/**
 * Uniform error response body returned by GlobalExceptionHandler for all error paths.
 *
 * Example JSON:
 * {
 *   "status":    404,
 *   "code":      "ORDER_NOT_FOUND",
 *   "message":   "Order with id 123 not found",
 *   "timestamp": "2026-03-02T10:00:00Z",
 *   "path":      "/api/orders/123"
 * }
 */
public record ErrorResponse(
        int status,
        String code,
        String message,
        Instant timestamp,
        String path
) {
    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(status, code, message, Instant.now(), path);
    }
}
