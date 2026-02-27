<!-- Last updated: 2026-02-27 by AIFSD team — initial sample for e-commerce reference domain -->

# Integrations — E-Commerce Sample

> This is a **sample** file showing how to populate `integrations.md` for a fictional e-commerce platform.
> Copy the structure (not the content) into your project's `integrations.md`.

## Events Published

| Event Name | Source Service | Kafka Topic | Payload Summary | Consumers | Trigger |
|-----------|---------------|-------------|-----------------|-----------|---------|
| OrderCreated | order-service | `order.events` | `{ orderId, customerId, items[{productId, sku, quantity, unitPrice}], totalAmount, shippingAddress, createdAt }` | payment-service, notification-service, analytics-service | Order is successfully created and persisted |
| OrderStatusChanged | order-service | `order.events` | `{ orderId, customerId, previousStatus, newStatus, reason, changedAt }` | notification-service, analytics-service | Any order state transition (CONFIRMED, SHIPPED, DELIVERED, CANCELLED) |
| OrderCancelled | order-service | `order.events` | `{ orderId, customerId, reason, cancelledAt, refundEligible }` | payment-service (triggers refund), inventory-service (releases stock), notification-service | Order is cancelled by customer or system |
| PaymentCompleted | payment-service | `payment.events` | `{ paymentId, orderId, amount, method, gatewayTransactionId, completedAt }` | order-service (confirms order), notification-service | Payment gateway confirms successful charge |
| PaymentFailed | payment-service | `payment.events` | `{ paymentId, orderId, amount, method, failureReason, failedAt }` | order-service (cancels order), notification-service | Payment gateway rejects charge |
| PaymentRefunded | payment-service | `payment.events` | `{ paymentId, orderId, refundAmount, reason, refundedAt }` | order-service, notification-service, analytics-service | Refund processed successfully |
| ShipmentCreated | fulfilment-service | `fulfilment.events` | `{ shipmentId, orderId, trackingNumber, carrier, estimatedDelivery, shippedAt }` | order-service (updates to SHIPPED), notification-service (sends tracking email) | Warehouse confirms dispatch |
| ShipmentDelivered | fulfilment-service | `fulfilment.events` | `{ shipmentId, orderId, deliveredAt, signedBy }` | order-service (updates to DELIVERED), notification-service | Carrier confirms delivery |
| CustomerCreated | customer-service | `customer.events` | `{ customerId, email, firstName, lastName, createdAt }` | notification-service (sends welcome email), analytics-service | New customer completes registration |
| ProductStockUpdated | catalog-service | `catalog.events` | `{ productId, sku, previousQuantity, newQuantity, reason, updatedAt }` | analytics-service | Stock quantity changes (restock, reservation, return) |

## Events Consumed

| Event Name | Source | Kafka Topic | Handler | Side Effects |
|-----------|--------|-------------|---------|--------------|
| PaymentCompleted | payment-service | `payment.events` | `OrderPaymentHandler.handlePaymentCompleted()` | Transitions Order CREATED → CONFIRMED; triggers stock reservation via inventory-service API call |
| PaymentFailed | payment-service | `payment.events` | `OrderPaymentHandler.handlePaymentFailed()` | Transitions Order to CANCELLED; no stock adjustment needed (stock wasn't reserved yet) |
| ShipmentCreated | fulfilment-service | `fulfilment.events` | `OrderFulfilmentHandler.handleShipmentCreated()` | Transitions Order CONFIRMED → SHIPPED; stores tracking number on Order |
| ShipmentDelivered | fulfilment-service | `fulfilment.events` | `OrderFulfilmentHandler.handleShipmentDelivered()` | Transitions Order SHIPPED → DELIVERED; starts 30-day refund window countdown |
| OrderCreated | order-service | `order.events` | `PaymentInitiationHandler.handleOrderCreated()` | Automatically initiates payment processing via payment gateway |
| OrderCancelled | order-service | `order.events` | `InventoryReleaseHandler.handleOrderCancelled()` | Releases reserved stock back to Product.stockQuantity |

## External Service Contracts

| Service | Direction | Protocol | Endpoint / Method | Auth | Purpose | Notes |
|---------|-----------|----------|-------------------|------|---------|-------|
| Stripe (Payment Gateway) | Outbound | REST (HTTPS) | `POST /v1/charges` | API key (`env: STRIPE_API_KEY`) | Process card payments for orders | Idempotency key = `orderId`; timeout = 10s; retry 2× on 5xx with exponential backoff; circuit breaker opens after 5 consecutive failures |
| Stripe (Refunds) | Outbound | REST (HTTPS) | `POST /v1/refunds` | API key (`env: STRIPE_API_KEY`) | Process refunds for cancelled/returned orders | Idempotency key = `paymentId-refund`; partial refunds supported |
| FedEx Shipping API | Outbound | REST (HTTPS) | `POST /ship/v1/shipments` | OAuth2 client credentials (`env: FEDEX_CLIENT_ID`, `FEDEX_CLIENT_SECRET`) | Create shipment and obtain tracking number | Rate-limited to 100 req/min; circuit breaker with 30s open window; timeout = 15s |
| FedEx Tracking API | Outbound | REST (HTTPS) | `GET /track/v1/trackingnumbers` | OAuth2 client credentials | Poll shipment delivery status | Polled every 30 min for in-transit shipments via scheduled job |
| SendGrid (Email) | Outbound | REST (HTTPS) | `POST /v3/mail/send` | API key (`env: SENDGRID_API_KEY`) | Send transactional emails (order confirmation, shipping notification, etc.) | Fire-and-forget; failures are logged but do not block order flow; retry 1× on 5xx |
| Catalog Service | Internal | REST (HTTPS) | `GET /api/v1/products/{id}` | Service-to-service JWT (`env: CATALOG_SERVICE_JWT_SECRET`) | Retrieve product details and current price during order creation | Response cached in Redis for 5 min; fallback to stale cache on timeout (10s) |
| Customer Service | Internal | REST (HTTPS) | `GET /api/v1/customers/{id}` | Service-to-service JWT | Retrieve customer profile and account status during order creation | Response cached in Redis for 2 min |
| Inventory Service | Internal | REST (HTTPS) | `POST /api/v1/inventory/reserve` | Service-to-service JWT | Reserve stock when an order is confirmed | Request body: `{ productId, quantity }`; returns 200 on success, 409 on insufficient stock |

## Anti-Corruption Rules

- **Never import entity classes from another service's package** — Use DTOs at service boundaries. For example, `order-service` must not import `com.example.catalog.Product`; instead, it receives a `ProductDTO` from the Catalog API and maps it to a local `ProductSnapshot` value object.

- **Never directly query another service's database** — All cross-service data access goes through the owning service's REST API or Kafka events. Cross-schema joins are strictly forbidden, even in read-only/reporting scenarios (use a dedicated read model or materialized view instead).

- **Shared kernel types live in `shared-kernel` module** — Shared value objects (`Money`, `AuditMetadata`, `PageResponse`) live in a dedicated `shared-kernel` Maven module. Changes to shared kernel require approval from all consuming service teams.

- **Event payloads must be self-contained** — Consumers must not need to call back to the producer to fully process an event. Include all necessary data in the payload. Example: `OrderCreated` includes the full `shippingAddress` snapshot, not just a customerId that requires a lookup.

- **API versioning is mandatory** — All inter-service REST APIs must be versioned: `/api/v1/...`. Breaking changes (field removal, type change, semantic change) require a new version (`/api/v2/...`). Old versions are supported for at least 2 release cycles (typically 3 months).

- **Event schema evolution** — New fields can be added to event payloads (backward-compatible). Existing fields must never be removed or have their type changed. If a breaking schema change is necessary, publish a new event name (e.g., `OrderCreatedV2`) and deprecate the old one with a 2-sprint sunset window.

- **Timeout and resilience defaults** — All outbound HTTP calls must specify: timeout (default: 10s), retry policy (default: 2× with exponential backoff), and circuit breaker (default: open after 5 failures, half-open after 30s). These are configured centrally in `application.yml` and overridden per-client only when explicitly justified.
