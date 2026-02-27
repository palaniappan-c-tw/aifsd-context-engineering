---
applyTo: "**/*.java"
---

# Java / Spring Boot — Coding Standards

> Auto-loaded when Copilot works on `*.java` files.
> These rules supplement the always-on conventions in `copilot-instructions.md`.
> Testing conventions live separately in `test.instructions.md`.
> Database / migration conventions live in `database.instructions.md`.

## Layering Rules

<!-- Controller → Service → Repository only. No skipping layers.
     Services must never return JPA entities — map to Response DTO.
     Constructor injection only — never @Autowired field injection. -->

## Naming Conventions

<!--
| Artifact         | Pattern                        | Example                     |
|------------------|--------------------------------|-----------------------------|
| Request DTO      | `<Action><Entity>Request`      | `CreateOrderRequest`        |
| Response DTO     | `<Entity>Response`             | `OrderResponse`             |
| Service          | `<Entity>Service`              | `OrderService`              |
| Controller       | `<Entity>Controller`           | `OrderController`           |
| Kafka Consumer   | `<Entity>EventConsumer`        | `PaymentEventConsumer`      |
| Kafka Producer   | `<Entity>EventPublisher`       | `OrderEventPublisher`       |
| Constants        | `UPPER_SNAKE_CASE`             | `MAX_RETRY_COUNT`           |
-->

## API Standards

<!-- All endpoints return ApiResponse<T>.
     All @RequestBody parameters must have @Valid.
     All endpoints must have @PreAuthorize.
     Never return raw entity from Controller — use @ControllerAdvice for error responses. -->

## Exception Handling

<!-- Never swallow exceptions with empty catch blocks.
     @Async methods must define AsyncUncaughtExceptionHandler.
     CompletableFuture chains must have .exceptionally() or .handle().
     Always log with context: log.error("Failed to process payment. orderId={}, amount={}", orderId, amount, ex); -->

## Security

<!-- Never concatenate user input into JPQL — use named parameters.
     Never log passwords, tokens, card numbers, or PII.
     Never map request DTOs to entities via ModelMapper without field whitelisting. -->

## Database & JPA

<!-- Never traverse lazy relationships inside a loop — use JOIN FETCH or @EntityGraph.
     Never call findAll() and filter in-memory — filter in the query.
     @Transactional on Service layer only — never Controller or Repository.
     @Transactional(readOnly = true) on all read-only Service methods.
     Never include external HTTP or Kafka calls inside @Transactional boundary. -->

## External HTTP Calls

<!-- All HTTP clients must have explicit connection and read timeouts.
     Feign clients must be wrapped with Resilience4j @CircuitBreaker.
     @Retry applies to idempotent operations only.
     Catch FeignException or HttpClientErrorException specifically — not broad Exception. -->

## Kafka

<!-- @KafkaListener methods must be idempotent — check processed event ID before acting.
     @KafkaListener methods must use @RetryableTopic with DLT configured.
     Never call producer.send() inside a @Transactional boundary — use Transactional Outbox pattern.
     Producer send failures must be handled — never fire-and-forget. -->

## Logging

<!-- Use SLF4J only — never System.out.println.
     Use parameterised statements — never string concatenation.
     INFO for normal ops, WARN for recoverable issues, ERROR for failures needing attention. -->

## Performance

<!-- Eliminate O(n²) operations — nested loops searching same collection must use HashMap keyed lookup.
     Replace List.contains() over large collections with HashSet or HashMap. -->

## Clean Code

<!-- Extract any conditional longer than 2 conditions into a named method.
     No method exceeds 20 lines. No class exceeds 200 lines.
     Delete dead code — never comment it out.
     Prefer Optional<T> return types in Service methods over returning null.
     Check for null and empty before iterating any collection. -->

## Backward Compatibility

<!-- Any changed Service method signature or REST API contract must provide a deprecation path before removal.
     Flag any breaking change without a migration or versioning strategy. -->
