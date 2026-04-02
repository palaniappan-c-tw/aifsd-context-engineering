<!-- Last updated: 2026-02-27 by AIFSD team — initial sample for e-commerce reference domain -->

# Domain Model — E-Commerce Sample

> This is a **sample** file showing how to populate `domain-model.md` for a fictional e-commerce platform.
> Copy the structure (not the content) into your project's `domain-model.md`.

## Entities

| Entity | Definition | Owning Service | Key Fields | Lifecycle States | Relationships |
|--------|-----------|----------------|------------|------------------|---------------|
| Order | A request to purchase one or more products, placed by a customer | order-service | id (UUID), customerId, status, totalAmount (Money), createdAt, updatedAt | CREATED → CONFIRMED → SHIPPED → DELIVERED / CANCELLED | Has many OrderItems; belongs to Customer; has one Payment |
| OrderItem | A line item within an Order representing a specific product and quantity | order-service | id (UUID), orderId, productId, sku, productName, quantity (int), unitPrice (Money) | — (lifecycle tied to parent Order) | Belongs to Order; references Product (snapshot) |
| Customer | A registered user who can browse products and place orders | customer-service | id (UUID), email, firstName, lastName, accountStatus, createdAt | ACTIVE → SUSPENDED → CLOSED | Has many Orders; has many Addresses |
| Address | A postal address associated with a Customer for shipping or billing | customer-service | id (UUID), customerId, type (SHIPPING/BILLING), street, city, state, zipCode, country, isDefault | — | Belongs to Customer; referenced by Order (as shipping/billing address snapshot) |
| Product | An item available for purchase in the catalog | catalog-service | id (UUID), sku, name, description, price (Money), stockQuantity (int), isActive, categoryId | DRAFT → ACTIVE → DISCONTINUED | Belongs to Category; referenced by OrderItem |
| Category | A classification group for products | catalog-service | id (UUID), name, slug, parentCategoryId, isActive | ACTIVE / ARCHIVED | Has many Products; optionally belongs to parent Category |
| Payment | A payment attempt against an Order | payment-service | id (UUID), orderId, amount (Money), method (CARD/BANK/WALLET), status, gatewayTransactionId, processedAt | PENDING → COMPLETED / FAILED / REFUNDED | Belongs to Order |
| Shipment | A physical shipment dispatched to fulfil an Order | fulfilment-service | id (UUID), orderId, trackingNumber, carrier, status, shippedAt, deliveredAt | CREATED → IN_TRANSIT → DELIVERED / RETURNED | Belongs to Order |

## Aggregates & Value Objects

### Order Aggregate
- **Aggregate Root**: Order
- **Child Entities**: OrderItem
- **Value Objects**: Money (amount: BigDecimal + currency: String), AddressSnapshot (immutable copy of Address at order time)
- **Invariant**: `Order.totalAmount` must equal `SUM(item.unitPrice × item.quantity)` for all OrderItems.
- **Invariant**: An Order must have at least 1 and at most 50 OrderItems.

### Customer Aggregate
- **Aggregate Root**: Customer
- **Child Entities**: Address
- **Value Objects**: Email (validated format), FullName (firstName + lastName)
- **Invariant**: A Customer must have exactly one default shipping Address when placing an order.

### Product Aggregate
- **Aggregate Root**: Product
- **Child Entities**: —
- **Value Objects**: Money (price), SKU (validated format: `[A-Z]{3}-[0-9]{6}`)
- **Invariant**: `Product.stockQuantity` must be ≥ 0 (no negative stock).

### Payment Aggregate
- **Aggregate Root**: Payment
- **Child Entities**: —
- **Value Objects**: Money (amount)
- **Invariant**: `Payment.amount` must equal `Order.totalAmount` for the associated Order.

## Entity Lifecycle State Machines

### Order Lifecycle

```
CREATED ──→ CONFIRMED ──→ SHIPPED ──→ DELIVERED
  │              │
  ▼              ▼
CANCELLED    CANCELLED
```

**Transition rules**:
- **CREATED → CONFIRMED**: Payment must be COMPLETED. Triggered automatically by `PaymentCompleted` event.
- **CONFIRMED → SHIPPED**: Fulfilment service confirms dispatch. Triggered by `ShipmentCreated` event.
- **CONFIRMED → CANCELLED**: Allowed within 1 hour of confirmation (customer-initiated). Stock reservation is released.
- **SHIPPED → DELIVERED**: Carrier confirms delivery. Triggered by `ShipmentDelivered` event.
- **CREATED → CANCELLED**: Allowed at any time before confirmation (customer-initiated or payment failure).
- **DELIVERED, CANCELLED**: Terminal states — no further transitions.

### Customer Account Lifecycle

```
ACTIVE ──→ SUSPENDED ──→ CLOSED
  │                         ▲
  └─────────────────────────┘
```

**Transition rules**:
- **ACTIVE → SUSPENDED**: Admin action (policy violation, fraud suspicion). All pending orders are paused.
- **SUSPENDED → ACTIVE**: Admin reinstatement after review.
- **SUSPENDED → CLOSED**: Automatic after 90 days of suspension with no reinstatement.
- **ACTIVE → CLOSED**: Customer self-service account deletion. PII is anonymized after 30-day grace period.

### Product Lifecycle

```
DRAFT ──→ ACTIVE ──→ DISCONTINUED
             │
             ▼
          ACTIVE (re-enabled)
```

**Transition rules**:
- **DRAFT → ACTIVE**: Product passes validation (has name, SKU, price > 0, at least one image). Published by catalog admin.
- **ACTIVE → DISCONTINUED**: Product is removed from sale. Existing orders referencing it are unaffected (they hold snapshots).
- **DISCONTINUED → ACTIVE**: Allowed — product can be re-listed if brought back into stock.

### Payment Lifecycle

```
PENDING ──→ COMPLETED
  │
  ├──→ FAILED
  │
  └──→ REFUNDED (only from COMPLETED)
```

**Transition rules**:
- **PENDING → COMPLETED**: Payment gateway confirms charge success.
- **PENDING → FAILED**: Payment gateway rejects charge. Order transitions to CANCELLED.
- **COMPLETED → REFUNDED**: Admin-initiated or automatic (within refund policy window). Triggers `PaymentRefunded` event.
