---
applyTo: "**/*Test.java,**/*Tests.java"
---

# Testing — Conventions & Standards

> Auto-loaded when Copilot works on test files (`*Test.java`, `*Tests.java`).
> These rules supplement the coding standards in `java.instructions.md`.

## Unit Tests (JUnit 5 + Mockito)

<!-- Every new or modified @Service method must have a unit test for the happy path.
     Every edge case (null, empty collection, failed external call) must have a corresponding test.
     Never mock the class under test.
     Tests must assert on observable output or state — not just verify a mock was called.
     Use @DisplayName for human-readable test names.
     Follow Arrange-Act-Assert (AAA) pattern. -->

## Slice Tests — Web Layer (@WebMvcTest)

<!-- New endpoints must have a @WebMvcTest slice test covering:
     - Request/response contract (status code, body shape).
     - @PreAuthorize behaviour (authorized vs. unauthorized).
     - Input validation (@Valid constraints).
     Use MockMvc; mock only the service layer. -->

## Slice Tests — Data Layer (@DataJpaTest)

<!-- Custom queries must be tested with @DataJpaTest + Testcontainers — never mock the DB for query correctness.
     Test with realistic data volumes where performance matters.
     Verify Flyway migrations run cleanly in test context. -->

## Integration Tests

<!-- Use Testcontainers for external dependencies (PostgreSQL, Kafka, Redis).
     Integration tests must be independent — no shared mutable state between tests.
     Suffix integration tests with `IT` (e.g., `OrderServiceIT.java`). -->

## Test Data

<!-- Use builder patterns or test fixtures for creating test data — avoid raw constructors with many arguments.
     Keep test data minimal — only include fields relevant to the scenario.
     Never use production data or PII in tests. -->

## What NOT to Do

<!-- Never test implementation details — test behaviour.
     Never write tests that only pass because of test execution order.
     Never ignore or @Disable tests without a linked issue/ticket.
     Never catch exceptions in tests — let them propagate for clearer failure messages. -->
