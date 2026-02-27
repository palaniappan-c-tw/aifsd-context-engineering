# Integrations

> Events published/consumed, external service contracts, and anti-corruption boundaries.
> This file defines how services communicate and where integration boundaries exist.
>
> **Keep this file up to date** — add new events, contracts, or boundary rules as they emerge.

## Events Published

> Async events this project publishes for other services to consume.

<!-- Example rows:

| Event Name | Source Service | Kafka Topic | Payload Summary | Consumers | Trigger |
|-----------|---------------|-------------|-----------------|-----------|---------|
| OrderCreated | order-service | order.events | `{ orderId, customerId, items[], totalAmount, createdAt }` | payment-service, notification-service | Order is successfully created |
| OrderStatusChanged | order-service | order.events | `{ orderId, previousStatus, newStatus, changedAt }` | notification-service, analytics-service | Any order state transition |
| PaymentCompleted | payment-service | payment.events | `{ paymentId, orderId, amount, method, completedAt }` | order-service, notification-service | Payment gateway confirms success |
| PaymentFailed | payment-service | payment.events | `{ paymentId, orderId, reason, failedAt }` | order-service, notification-service | Payment gateway returns failure |
-->

| Event Name | Source Service | Kafka Topic | Payload Summary | Consumers | Trigger |
|-----------|---------------|-------------|-----------------|-----------|---------|
|           |               |             |                 |           |         |

## Events Consumed

> Async events this project listens to and processes.

<!-- Example rows:

| Event Name | Source | Kafka Topic | Handler | Side Effects |
|-----------|--------|-------------|---------|--------------|
| PaymentCompleted | payment-service | payment.events | OrderPaymentHandler | Transitions Order from CREATED → CONFIRMED; triggers stock reservation |
| PaymentFailed | payment-service | payment.events | OrderPaymentHandler | Transitions Order to CANCELLED; notifies customer |
| InventoryReserved | inventory-service | inventory.events | OrderFulfilmentHandler | Transitions Order from CONFIRMED → SHIPPED when warehouse confirms |
-->

| Event Name | Source | Kafka Topic | Handler | Side Effects |
|-----------|--------|-------------|---------|--------------|
|           |        |             |         |              |

## External Service Contracts

> Synchronous calls to or from external services (REST, gRPC, etc).

<!-- Example rows:

| Service | Direction | Protocol | Endpoint / Method | Auth | Purpose | Notes |
|---------|-----------|----------|-------------------|------|---------|-------|
| Payment Gateway (Stripe) | Outbound | REST | POST /v1/charges | API key (env: STRIPE_API_KEY) | Process payment for an Order | Idempotency key = orderId; timeout = 10s; retry 2× on 5xx |
| Catalog Service | Inbound call | REST | GET /api/products/{id} | Service-to-service JWT | Retrieve product details and current price | Cache for 5 min; fallback to stale cache on timeout |
| Notification Service | Outbound | REST | POST /api/notifications | Service-to-service JWT | Send email/SMS to customer | Fire-and-forget; failures logged but do not block order flow |
| Shipping Provider (FedEx) | Outbound | REST | POST /ship/v1/shipments | OAuth2 client credentials | Create shipment and get tracking number | Rate-limited to 100 req/min; circuit breaker with 30s open window |
-->

| Service | Direction | Protocol | Endpoint / Method | Auth | Purpose | Notes |
|---------|-----------|----------|-------------------|------|---------|-------|
|         |           |          |                   |      |         |       |

## Anti-Corruption Rules

> Boundaries that must not leak between contexts. These rules prevent tight coupling.

<!-- Example entries:

- **Never import entity classes from another context's package** — use DTOs at service boundaries. E.g., `order-service` must not import `com.example.catalog.Product`; instead, use a `ProductSnapshot` DTO.
- **Never directly query another context's database tables** — go through the owning service's API or consume events. Cross-schema joins are forbidden.
- **Shared kernel types (if any) live in a dedicated `shared-kernel` module** — e.g., `Money`, `AuditMetadata`. Changes to shared kernel require approval from all consuming teams.
- **Event payloads must be self-contained** — consumers must not need to call back to the producer to understand an event. Include all necessary data in the payload.
- **API versioning** — all inter-service REST APIs must be versioned (e.g., `/api/v1/...`). Breaking changes require a new version; old versions are supported for at least 2 release cycles.
-->
