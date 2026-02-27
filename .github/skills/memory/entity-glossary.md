# Entity Glossary

> Core domain entities, their definitions, owning services, and key relationships.
> Keep this up to date as the domain evolves.

| Entity | Definition | Owning Service | Key Relationships | Notes |
|--------|-----------|----------------|-------------------|-------|
| <!-- e.g., Order --> | <!-- A request to purchase one or more products --> | <!-- order-service --> | <!-- Has many OrderItems; belongs to Customer --> | <!-- --> |
| <!-- e.g., Customer --> | <!-- A registered user who can place orders --> | <!-- customer-service --> | <!-- Has many Orders; has one Address --> | <!-- --> |
| <!-- e.g., Product --> | <!-- An item available for purchase --> | <!-- catalog-service --> | <!-- Belongs to Category; referenced by OrderItem --> | <!-- --> |

## Domain Rules

<!-- List critical business rules that affect code generation. Examples:
     - An Order cannot be placed if the Customer's account is suspended.
     - OrderItem quantity must be > 0 and <= available stock.
     - A Product's price is immutable once an Order references it — use price snapshots. -->
