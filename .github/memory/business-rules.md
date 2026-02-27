# Business Rules

> Invariants, validations, policies, and domain terminology that govern code behaviour.
> These rules **must** be respected by generated code. When in doubt, enforce the rule.
>
> **Keep this file up to date** — add new rules as they are discovered or refined.

## Invariants

> Hard rules that must **always** hold true. Violations indicate a bug.

<!-- Example rows:

| ID | Rule | Applies To | Enforcement Point | Notes |
|----|------|-----------|-------------------|-------|
| INV-001 | An Order cannot be placed if the Customer's account is SUSPENDED or CLOSED | Order creation | OrderService.createOrder() | Return 403 with clear message |
| INV-002 | OrderItem.quantity must be > 0 and ≤ available stock at time of order | OrderItem validation | OrderService.createOrder() | Check stock via Catalog API; race condition handled by optimistic lock |
| INV-003 | Order.totalAmount must equal SUM(item.unitPrice × item.quantity) for all items | Order consistency | Order aggregate | Recalculate on every item add/remove |
| INV-004 | A Product's price is immutable once an Order references it — use price snapshots | Price integrity | OrderItem.unitPrice | Snapshot price at order creation time, never update retroactively |
-->

| ID | Rule | Applies To | Enforcement Point | Notes |
|----|------|-----------|-------------------|-------|
|    |      |           |                   |       |

## Validations

> Input validation rules — field-level constraints, format rules, and allowed values.

<!-- Example rows:

| Field | Rule | Error Code / Message |
|-------|------|---------------------|
| Customer.email | Must be a valid email format; must be unique across all customers | INVALID_EMAIL / EMAIL_ALREADY_EXISTS |
| Order.items | Must contain at least 1 item; maximum 50 items per order | ORDER_EMPTY / ORDER_TOO_LARGE |
| Product.price | Must be > 0; precision = 2 decimal places; currency = USD | INVALID_PRICE |
| Product.sku | Must match pattern `[A-Z]{3}-[0-9]{6}`; must be unique | INVALID_SKU / SKU_ALREADY_EXISTS |
| Address.zipCode | Must match pattern `[0-9]{5}(-[0-9]{4})?` for US addresses | INVALID_ZIP |
-->

| Field | Rule | Error Code / Message |
|-------|------|---------------------|
|       |      |                     |

## Policies

> Business policies that affect code flow, timing, or feature behaviour.
> These are softer than invariants — they may change with business decisions.

<!-- Example entries:

- **Refund window**: Refunds are allowed within 30 days of delivery. After 30 days, only store credit is issued.
- **Price snapshot**: When an Order is created, the current Product price is copied to OrderItem.unitPrice. All subsequent calculations use this snapshot, never the current catalog price.
- **Soft delete**: Customers are never hard-deleted. Account closure sets status = CLOSED and anonymizes PII after 90 days.
- **Stock reservation**: Stock is reserved (decremented) when an Order is CONFIRMED, not when CREATED. If payment fails, reserved stock is released.
- **Rate limiting**: A single Customer can place a maximum of 10 orders per hour. Exceeding this triggers a temporary 15-minute cooldown.
-->

## Terminology

> Ubiquitous language — domain terms that must be used consistently in code, APIs, and documentation.
> When a business term differs from a common technical name, document it here.

<!-- Example rows:

| Business Term | Technical Mapping | Do NOT Use | Notes |
|--------------|-------------------|-----------|-------|
| Order | `Order` entity, `orders` table | Purchase, Transaction, Cart | "Cart" is a separate pre-order concept |
| Fulfilment | The process from CONFIRMED → DELIVERED | Shipping (too narrow) | Includes picking, packing, shipping, and delivery confirmation |
| SKU | Stock Keeping Unit — unique product identifier | Product ID (ambiguous) | SKU is the business identifier; `id` is the DB primary key |
| Backorder | An order accepted when stock = 0, fulfilled when stock replenishes | Out of stock (that's a state, not an action) | Allowed only for products with `allowBackorder = true` |
-->

| Business Term | Technical Mapping | Do NOT Use | Notes |
|--------------|-------------------|-----------|-------|
|              |                   |           |       |
