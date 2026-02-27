# Bounded Contexts

> Service boundaries, responsibilities, and inter-service communication contracts.
> This defines what each service owns and how services interact.

## Context Map

<!-- High-level diagram or description of how bounded contexts relate to each other.
     e.g., Order Context → (publishes OrderCreated event) → Payment Context
           Payment Context → (calls) → Notification Context -->

## Contexts

### <!-- e.g., Order Context -->

| Aspect | Detail |
|--------|--------|
| **Service** | <!-- e.g., order-service --> |
| **Responsibility** | <!-- e.g., Order lifecycle management — creation, status transitions, cancellation --> |
| **Key Entities** | <!-- e.g., Order, OrderItem, OrderStatus --> |
| **Upstream Dependencies** | <!-- e.g., Catalog Context (reads product info), Customer Context (reads customer info) --> |
| **Downstream Consumers** | <!-- e.g., Payment Context (listens for OrderCreated), Notification Context (listens for OrderStatusChanged) --> |
| **Communication** | <!-- e.g., Publishes Kafka events: OrderCreated, OrderStatusChanged. Consumes: PaymentCompleted --> |

### <!-- e.g., Payment Context -->

| Aspect | Detail |
|--------|--------|
| **Service** | <!-- --> |
| **Responsibility** | <!-- --> |
| **Key Entities** | <!-- --> |
| **Upstream Dependencies** | <!-- --> |
| **Downstream Consumers** | <!-- --> |
| **Communication** | <!-- --> |

## Anti-Corruption Rules

<!-- Rules that prevent context boundaries from leaking. Examples:
     - Never import entity classes from another context's package — use DTOs at the boundary.
     - Never directly query another context's database tables — go through the API or consume events.
     - Shared kernel types (if any) live in a dedicated shared module. -->
