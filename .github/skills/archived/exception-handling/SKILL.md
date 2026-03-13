---
name: exception-handling
description: 'Workflow for scaffolding a consistent exception handling strategy in a Java Spring Boot project. Use this skill whenever a user asks to set up exception handling, add a custom exception, create a GlobalExceptionHandler, define an error response format, or asks how exceptions should be structured or handled across layers. Also use this when adding a new AppException subtype or when a service needs to wrap a third-party exception.'
---

# Exception Handling Workflow

Step-by-step procedure for scaffolding a consistent, project-wide exception handling strategy.

> **Before starting**: This skill defines the structure and wiring. Rules for logging format,
> `@Async` error handlers, and Feign-specific catch clauses are enforced by
> `.github/instructions/java.instructions.md` — do not duplicate them here.

---

## Procedure

### Step 1: Understand the Scope

Ask the user (or infer from context) which of the following applies:

| Scenario | Jump to |
|---|---|
| First-time setup — nothing exists yet | Steps 2 → 5 in order |
| Adding a new exception type only | Step 2 only |
| Wiring the handler for the first time | Steps 3 → 4 |
| Wrapping a third-party exception | Step 5 |

---

### Step 2: Define the Exception Hierarchy

Use [templates/AppException.java](templates/AppException.java) as the base.

**Hierarchy:**

```
AppException (abstract, RuntimeException)
├── ResourceNotFoundException     → HTTP 404
├── BusinessRuleViolationException → HTTP 422
├── ConflictException              → HTTP 409
└── ExternalServiceException       → HTTP 502
```

**Rules:**
- All custom exceptions extend `AppException`.
- Every exception carries a `code` string (e.g. `ORDER_NOT_FOUND`, `PAYMENT_LIMIT_EXCEEDED`).
- Codes are `UPPER_SNAKE_CASE` and scoped to the domain (prefer `<DOMAIN>_<REASON>`).
- If none of the four built-in subtypes fit, create a new subtype of `AppException` — never throw `AppException` directly.

Place exception classes in: `src/main/java/<base-package>/exception/`

---

### Step 3: Create the ErrorResponse

Use [templates/ErrorResponse.java](templates/ErrorResponse.java).

**Shape:**
```json
{
  "status": 404,
  "code": "ORDER_NOT_FOUND",
  "message": "Order with id 123 not found",
  "timestamp": "2026-03-02T10:00:00Z",
  "path": "/api/orders/123"
}
```

Place in: `src/main/java/<base-package>/exception/`

---

### Step 4: Wire the GlobalExceptionHandler

Use [templates/GlobalExceptionHandler.java](templates/GlobalExceptionHandler.java).

**Handlers to include (in order):**

| Exception | Status | Log level |
|---|---|---|
| `ResourceNotFoundException` | 404 | `WARN` |
| `BusinessRuleViolationException` | 422 | `WARN` |
| `ConflictException` | 409 | `WARN` |
| `ExternalServiceException` | 502 | `ERROR` (include stack trace) |
| `MethodArgumentNotValidException` | 400 | `WARN` |
| `Exception` (catch-all) | 500 | `ERROR` (include stack trace) |

**Rules:**
- The catch-all `Exception` handler must be last and must never expose internal details in the response `message` — use a generic string like `"An unexpected error occurred"`.
- Place in: `src/main/java/<base-package>/exception/`

---

### Step 5: Apply Layer Rules

Each layer has a single responsibility regarding exceptions — do not skip layers.

#### Repository Layer
- **Do not catch** Spring `DataAccessException` — Spring translates JPA/JDBC exceptions automatically.
- Only wrap if you need to add domain context; in that case throw `ExternalServiceException` with the cause.

#### Service Layer
- **This is where exceptions are thrown.** Inspect preconditions and domain state here.
- Use `ResourceNotFoundException` when a required entity is absent.
- Use `BusinessRuleViolationException` for domain rule failures.
- Use `ConflictException` for state collisions (e.g. duplicate creation).
- Do **not** catch `AppException` subtypes and rethrow — let them propagate.

```java
// Correct
Product product = productRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("PRODUCT_NOT_FOUND",
            "Product with id " + id + " not found"));

// Wrong — catching and rethrowing adds no value
try {
    return doSomething();
} catch (ResourceNotFoundException e) {
    throw e; // pointless
}
```

#### Controller Layer
- **Do not catch anything.** Let `GlobalExceptionHandler` handle all exceptions.
- Never return `null` or a raw HTTP 200 to signal a failure.

#### Feign / External HTTP Clients
- Wrap `FeignException` in `ExternalServiceException` inside the service method or a dedicated Feign `ErrorDecoder`:

```java
try {
    return paymentClient.charge(request);
} catch (FeignException ex) {
    throw new ExternalServiceException(
            "PAYMENT_SERVICE_UNAVAILABLE",
            "Payment service call failed",
            ex);
}
```

- Do not catch `Exception` broadly here — see `java.instructions.md` for the specific Feign catch rule.

---

## Quick Reference: Code → HTTP Status

| Code pattern | Exception class | Status |
|---|---|---|
| `*_NOT_FOUND` | `ResourceNotFoundException` | 404 |
| `*_LIMIT_*`, `*_INVALID_*`, `*_REQUIRED` | `BusinessRuleViolationException` | 422 |
| `*_ALREADY_EXISTS`, `*_CONFLICT` | `ConflictException` | 409 |
| `*_UNAVAILABLE`, `*_TIMEOUT` | `ExternalServiceException` | 502 |
| `VALIDATION_FAILED` | (auto — `@Valid`) | 400 |

---

## Resources

- [AppException.java template](templates/AppException.java)
- [ErrorResponse.java template](templates/ErrorResponse.java)
- [GlobalExceptionHandler.java template](templates/GlobalExceptionHandler.java)
