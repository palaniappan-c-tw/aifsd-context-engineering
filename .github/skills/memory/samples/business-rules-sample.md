<!-- Last updated: 2026-02-27 by AIFSD team — initial sample for e-commerce reference domain -->

# Business Rules — E-Commerce Sample

> This is a **sample** file showing how to populate `business-rules.md` for a fictional e-commerce platform.
> Copy the structure (not the content) into your project's `business-rules.md`.

## Invariants

| ID | Rule | Applies To | Enforcement Point | Notes |
|----|------|-----------|-------------------|-------|
| INV-001 | An Order cannot be placed if the Customer's account status is SUSPENDED or CLOSED | Order creation | `OrderService.createOrder()` | Return HTTP 403 with error code `CUSTOMER_NOT_ELIGIBLE` |
| INV-002 | OrderItem.quantity must be > 0 and ≤ available stock at time of order creation | OrderItem validation | `OrderService.createOrder()` | Check stock via Catalog API call; race condition mitigated by optimistic locking on Product.stockQuantity |
| INV-003 | Order.totalAmount must equal SUM(item.unitPrice × item.quantity) for all OrderItems | Order consistency | `Order` aggregate (recalculated on every item add/remove) | If mismatch is detected, throw `OrderAmountMismatchException` |
| INV-004 | A Product's price is immutable once an OrderItem references it — use price snapshots | Price integrity | `OrderItem.unitPrice` is set at order creation | Never update OrderItem.unitPrice after creation; always snapshot from Product.price |
| INV-005 | Product.stockQuantity must never go below 0 | Stock integrity | `InventoryService.reserveStock()` | Use optimistic locking (`@Version`) on Product entity; throw `InsufficientStockException` on conflict |
| INV-006 | Payment.amount must equal Order.totalAmount | Payment integrity | `PaymentService.initiatePayment()` | Validated before calling payment gateway |
| INV-007 | A Customer must have a verified email before placing orders | Customer eligibility | `OrderService.createOrder()` | Check `Customer.emailVerified = true`; return `EMAIL_NOT_VERIFIED` if false |
| INV-008 | An Order in SHIPPED state cannot be cancelled | Order state protection | `OrderService.cancelOrder()` | Return HTTP 409 with `ORDER_ALREADY_SHIPPED` |

## Validations

| Field | Rule | Error Code / Message |
|-------|------|---------------------|
| Customer.email | Must be a valid email format (RFC 5322); must be unique across all customers | `INVALID_EMAIL` / `EMAIL_ALREADY_EXISTS` |
| Customer.firstName, Customer.lastName | Required; 1-100 characters; no special characters except hyphen and apostrophe | `INVALID_NAME` |
| Order.items | Must contain at least 1 item; maximum 50 items per order | `ORDER_EMPTY` / `ORDER_TOO_LARGE` |
| Product.price | Must be > 0; precision = 2 decimal places; currency must be a valid ISO 4217 code | `INVALID_PRICE` |
| Product.sku | Must match pattern `[A-Z]{3}-[0-9]{6}`; must be unique across all products | `INVALID_SKU` / `SKU_ALREADY_EXISTS` |
| Product.name | Required; 1-200 characters | `INVALID_PRODUCT_NAME` |
| Address.zipCode | Must match `[0-9]{5}(-[0-9]{4})?` for US addresses | `INVALID_ZIP` |
| Address.country | Must be a valid ISO 3166-1 alpha-2 country code | `INVALID_COUNTRY` |
| OrderItem.quantity | Must be an integer > 0 and ≤ 999 | `INVALID_QUANTITY` |
| Payment.method | Must be one of: `CARD`, `BANK_TRANSFER`, `WALLET` | `INVALID_PAYMENT_METHOD` |

## Policies

- **Refund window**: Refunds are allowed within 30 calendar days of delivery date. After 30 days, only store credit is issued. Refund amount = original OrderItem prices (not current catalog prices).

- **Price snapshot**: When an Order is created, the current `Product.price` is copied to `OrderItem.unitPrice`. All subsequent calculations (totals, refunds, reports) use this snapshot. Never use the current catalog price for an existing order.

- **Soft delete for Customers**: Customers are never hard-deleted from the database. Account closure (`status = CLOSED`) triggers PII anonymization after a 30-day grace period. During the grace period, the customer can reactivate. Anonymization replaces: email → `anon-{uuid}@deleted.local`, firstName/lastName → `[REDACTED]`.

- **Stock reservation timing**: Stock is reserved (decremented from `Product.stockQuantity`) when an Order transitions to CONFIRMED (i.e., after successful payment), not when CREATED. If payment fails, no stock adjustment is needed. This avoids holding stock for abandoned orders.

- **Rate limiting**: A single Customer can place a maximum of 10 orders per hour. Exceeding this triggers HTTP 429 with a `RATE_LIMIT_EXCEEDED` error code and a 15-minute cooldown. Tracked via sliding window counter in Redis.

- **Order cancellation grace period**: Customers can cancel a CONFIRMED order within 1 hour of confirmation at no cost. After 1 hour, cancellation incurs a 5% restocking fee deducted from the refund.

- **Product discontinuation**: When a Product is DISCONTINUED, it is hidden from catalog search results and browse pages but remains accessible via direct link for 30 days (for SEO and bookmark preservation). Existing orders referencing discontinued products are unaffected.

- **Currency**: All monetary amounts are stored and processed in USD. Multi-currency support is out of scope for v1. The `Money` value object always uses `USD` as currency.

## Terminology

| Business Term | Technical Mapping | Do NOT Use | Notes |
|--------------|-------------------|-----------|-------|
| Order | `Order` entity, `orders` table | Purchase, Transaction, Cart | "Cart" is a separate pre-order concept (not yet modelled in v1) |
| Fulfilment | The business process from CONFIRMED → DELIVERED, managed by fulfilment-service | Shipping (too narrow — fulfilment includes picking, packing, and delivery confirmation) | Maps to `Shipment` entity for the physical dispatch |
| SKU | Stock Keeping Unit — unique human-readable product identifier (`[A-Z]{3}-[0-9]{6}`) | Product Code, Product ID | SKU is the *business* identifier; `id` (UUID) is the *database* primary key |
| Backorder | An order accepted when stock = 0, fulfilled when stock is replenished | Out of stock (describes a state, not an action) | Not implemented in v1 — orders are rejected when stock = 0 |
| Price Snapshot | The `unitPrice` stored on OrderItem at creation time | Current price, live price | Emphasises immutability — once captured, it never changes |
| PII | Personally Identifiable Information (email, name, address) | User data (too vague) | Subject to anonymization policy on account closure |
| Grace Period | The 30-day window after account closure before PII is anonymized | Cooling-off period | Also used for the 1-hour cancellation window on confirmed orders (different duration) |
