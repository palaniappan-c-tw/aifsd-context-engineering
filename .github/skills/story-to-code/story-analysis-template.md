# Story Analysis Template

> Use this template to break down a Jira user story before generating code.
> Fill in each section to ensure a complete understanding of what needs to be built.

## Story Summary

- **Story ID**: <!-- e.g., PROJ-1234 -->
- **Title**: <!-- e.g., As a customer, I can cancel an order within 30 minutes of placement -->
- **Type**: <!-- Feature / Bug Fix / Refactor / Tech Debt -->

## Acceptance Criteria Breakdown

| # | Criterion | Testable Requirement | Affected Layer(s) |
|---|-----------|---------------------|--------------------|
| 1 | <!-- Given/When/Then from story --> | <!-- Specific assertion --> | <!-- API / Service / DB / UI --> |
| 2 | | | |
| 3 | | | |

## Affected Modules

| Module | Action | Details |
|--------|--------|---------|
| <!-- e.g., order-service --> | <!-- Create / Modify --> | <!-- New cancellation endpoint + service logic --> |
| <!-- e.g., notification-service --> | <!-- Modify --> | <!-- Listen for OrderCancelled event --> |

## API Changes

| Method | Endpoint | Request Body | Response Body | Auth |
|--------|----------|-------------|---------------|------|
| <!-- POST --> | <!-- /api/orders/{id}/cancel --> | <!-- CancelOrderRequest --> | <!-- ApiResponse<OrderResponse> --> | <!-- @PreAuthorize("hasRole('CUSTOMER')") --> |

## Database Changes

| Change | Script Name | Details |
|--------|------------|---------|
| <!-- Add column --> | <!-- V5__add_cancelled_at_to_orders.sql --> | <!-- cancelled_at TIMESTAMP NULL to orders table --> |

## Event Changes

| Event | Topic | Producer | Consumer(s) |
|-------|-------|----------|-------------|
| <!-- OrderCancelled --> | <!-- order-events --> | <!-- order-service --> | <!-- notification-service, inventory-service --> |

## Test Plan

| Test Type | What to Test | Framework |
|-----------|-------------|-----------|
| <!-- Unit --> | <!-- CancellationService.cancel() happy path + edge cases --> | <!-- JUnit 5 + Mockito --> |
| <!-- Slice --> | <!-- POST /api/orders/{id}/cancel contract + auth --> | <!-- @WebMvcTest --> |
| <!-- Integration --> | <!-- Cancellation query correctness --> | <!-- @DataJpaTest + Testcontainers --> |

## Open Questions

<!-- List anything unclear from the story that needs clarification before implementation. -->
