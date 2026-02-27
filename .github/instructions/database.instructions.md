---
applyTo: "**/db/migration/**,**/*.sql"
---

# Database & Migrations — Conventions

> Auto-loaded when Copilot works on SQL files or Flyway migration scripts.
> These rules supplement the coding standards in `java.instructions.md`.

## Flyway Naming

<!-- Script pattern: V<n>__<description>.sql  (e.g., V2__add_payment_index.sql)
     Never edit an existing Flyway script — create a new version.
     Use descriptive names: V3__create_order_table.sql, V4__add_status_column_to_orders.sql -->

## Schema Design

<!-- Every table must have a primary key.
     Use appropriate column types — avoid VARCHAR for everything.
     Add NOT NULL constraints where the domain requires it.
     Timestamp columns: use TIMESTAMPTZ, not TIMESTAMP. -->

## Indexing

<!-- New WHERE clause columns must have a migration adding the index.
     Composite indexes: put the most selective column first.
     Name indexes explicitly: idx_<table>_<columns>  (e.g., idx_orders_customer_id). -->

## Data Integrity

<!-- Use foreign keys for referential integrity where appropriate.
     Add CHECK constraints for domain invariants (e.g., quantity > 0).
     Use UNIQUE constraints to enforce business uniqueness rules. -->

## Migration Safety

<!-- Migrations must be backward-compatible with the currently running application version.
     Avoid DROP COLUMN in the same release as the code change — do it in the next release.
     Large data migrations should run in batches — never UPDATE without a WHERE clause.
     Always test migrations against a copy of production-like data. -->
