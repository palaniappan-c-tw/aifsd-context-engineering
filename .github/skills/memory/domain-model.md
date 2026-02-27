# Domain Model

> Core domain entities, their fields, relationships, lifecycle states, and aggregate boundaries.
> This is the single source of truth for how the business domain is modelled in code.
>
> **Keep this file up to date** — update it whenever entities, fields, or relationships change.

## Entities

<!-- Add one row per domain entity. Example rows are commented out below.

| Entity | Definition | Owning Service | Key Fields | Lifecycle States | Relationships |
|--------|-----------|----------------|------------|------------------|---------------|
| Order | A request to purchase one or more products | order-service | id, customerId, status, totalAmount, createdAt | CREATED → CONFIRMED → SHIPPED → DELIVERED / CANCELLED | Has many OrderItems; belongs to Customer |
| Customer | A registered user who can place orders | customer-service | id, email, name, accountStatus, createdAt | ACTIVE / SUSPENDED / CLOSED | Has many Orders; has one Address |
| Product | An item available for purchase | catalog-service | id, name, sku, price, stockQuantity, isActive | DRAFT → ACTIVE → DISCONTINUED | Belongs to Category; referenced by OrderItem |
| OrderItem | A line item within an Order | order-service | id, orderId, productId, quantity, unitPrice | — (lifecycle tied to parent Order) | Belongs to Order; references Product |
| Payment | A payment attempt against an Order | payment-service | id, orderId, amount, method, status, processedAt | PENDING → COMPLETED / FAILED / REFUNDED | Belongs to Order |
-->

| Entity | Definition | Owning Service | Key Fields | Lifecycle States | Relationships |
|--------|-----------|----------------|------------|------------------|---------------|
|        |           |                |            |                  |               |

## Aggregates & Value Objects

> Distinguish between **Aggregate Roots** (top-level entities that enforce consistency boundaries),
> **Child Entities** (exist only within an aggregate), and **Value Objects** (immutable, identity-less).

<!-- Example:

### Order Aggregate
- **Aggregate Root**: Order
- **Child Entities**: OrderItem
- **Value Objects**: Money (amount + currency), Address (street, city, zip, country)
- **Invariant**: Order.totalAmount must equal the sum of all OrderItem prices × quantities.

### Customer Aggregate
- **Aggregate Root**: Customer
- **Child Entities**: —
- **Value Objects**: Email, FullName, Address
- **Invariant**: A Customer must have a verified email before placing orders.
-->

## Entity Lifecycle State Machines

> Document state transitions for entities with non-trivial lifecycles.
> Use simple ASCII diagrams so the AI can reason about valid transitions.

<!-- Example:

### Order Lifecycle

```
CREATED ──→ CONFIRMED ──→ SHIPPED ──→ DELIVERED
  │              │
  ▼              ▼
CANCELLED    CANCELLED
```

**Transition rules**:
- CREATED → CONFIRMED: Payment must be completed.
- CONFIRMED → SHIPPED: Warehouse must confirm dispatch.
- CONFIRMED → CANCELLED: Only within 1 hour of confirmation.
- SHIPPED → DELIVERED: Carrier confirms delivery.
- CREATED → CANCELLED: Allowed at any time before confirmation.
- DELIVERED / CANCELLED are terminal states.

### Customer Account Lifecycle

```
ACTIVE ──→ SUSPENDED ──→ CLOSED
  │                         ▲
  └─────────────────────────┘
```

**Transition rules**:
- ACTIVE → SUSPENDED: Admin action or policy violation.
- SUSPENDED → ACTIVE: Admin reinstatement.
- SUSPENDED → CLOSED: After 90 days of suspension.
- ACTIVE → CLOSED: Customer self-service deletion request.
-->
