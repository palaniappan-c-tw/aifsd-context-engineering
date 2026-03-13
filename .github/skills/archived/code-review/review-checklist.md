# Code Review Checklist

> Systematic checklist for reviewing code quality. Referenced by the `code-review` skill.
> Check each category and flag issues as Blocker / Warning / Suggestion.

## Architecture Compliance

- [ ] Changes respect layer boundaries (Controller → Service → Repository).
- [ ] No business logic in Controllers or Repositories.
- [ ] Services don't return JPA entities across layer boundaries.
- [ ] Domain boundaries are respected — no cross-context entity imports.
- [ ] New files are in the correct package/directory per the module map.

## Security

- [ ] No user input concatenated into queries — parameterized only.
- [ ] No passwords, tokens, card numbers, or PII logged.
- [ ] All endpoints have appropriate `@PreAuthorize` / auth checks.
- [ ] DTO-to-entity mapping uses explicit field whitelisting.
- [ ] No secrets hardcoded — environment variables or secrets manager used.

## Error Handling

- [ ] No empty catch blocks — exceptions are logged with context or rethrown.
- [ ] `@ControllerAdvice` used for error responses — no try/catch in Controllers.
- [ ] Async operations have exception handlers configured.
- [ ] API errors return consistent `ApiResponse<T>` structure.
- [ ] Frontend displays user-friendly error messages — no raw error objects.

## Database & Data Access

- [ ] No N+1 queries — lazy relationships use JOIN FETCH or @EntityGraph.
- [ ] No in-memory filtering of full table scans — queries filter at DB level.
- [ ] `@Transactional` on Service layer only, `readOnly = true` where appropriate.
- [ ] No external calls (HTTP, Kafka) inside `@Transactional` boundaries.
- [ ] Flyway scripts follow naming conventions; existing scripts not modified.
- [ ] New query columns have indexes (with migration).

## Performance

- [ ] No O(n²) operations — nested loops use Map-based lookups.
- [ ] Large collections use HashSet/HashMap instead of List.contains().
- [ ] React components don't re-render unnecessarily — memoization where profiled.

## Testing

- [ ] New/modified service methods have unit tests (happy path + edge cases).
- [ ] New endpoints have slice tests covering contract and authorization.
- [ ] Custom queries tested with @DataJpaTest + Testcontainers.
- [ ] React components tested with React Testing Library (user behavior, not implementation).
- [ ] Tests assert on output/state — not just mock invocation verification.

## Naming & Standards

- [ ] Classes, methods, and variables follow project naming conventions.
- [ ] DTOs, services, controllers follow the naming pattern table.
- [ ] No magic numbers or strings — extracted to named constants.

## Clean Code

- [ ] No method exceeds 20 lines; no class exceeds 200 lines.
- [ ] Complex conditionals extracted into named methods.
- [ ] No dead code or commented-out code.
- [ ] `Optional<T>` preferred over returning null from Service methods.

## Backward Compatibility

- [ ] Changed API contracts have a deprecation path.
- [ ] Breaking changes flagged with migration/versioning strategy.
