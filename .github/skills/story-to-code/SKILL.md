---
name: story-to-code
description: 'Workflow for generating a compliant implementation from a Jira user story. Use this skill when asked to implement a story, feature, or user requirement end-to-end.'
---

# Story-to-Code Workflow

Step-by-step procedure for turning a Jira user story into a compliant, tested implementation.

## Procedure

### Step 1: Analyze the Story

Use the [Story Analysis Template](story-analysis-template.md) to break down the story:
- Extract acceptance criteria into testable requirements.
- Identify which bounded contexts and entities are involved (load the `memory` skill if needed).
- Determine affected layers (API / Service / Repository / Events / UI).

### Step 2: Plan the Changes

Before writing any code, produce a brief plan covering:
- **Files to create or modify** — list each file with its purpose.
- **API changes** — new or modified endpoints, request/response shapes.
- **Database changes** — new tables, columns, indexes, Flyway migration scripts.
- **Event changes** — new Kafka topics, event schemas.
- **Dependencies** — any new libraries or configurations needed.

--Review Plan Developer--

### Step 3: Generate the Implementation 

Generate code layer by layer, following the project's coding standards (loaded from `.github/instructions/`):
1. **Database migration** (if needed) — Flyway script following naming conventions.
2. **Domain / Entity** — JPA entity or React types as appropriate.
3. **Repository** — data access layer with custom queries as needed.
4. **Service** — business logic, mapping entities to DTOs.
5. **Controller / API** — endpoint with validation, authorization, error handling.
6. **Event producer/consumer** (if needed) — Kafka integration following outbox pattern.
7. **Frontend components** (if needed) — React components following team conventions.

### Step 4: Generate Tests

For each layer of code generated:
- Unit tests for service methods (happy path + edge cases).
- Slice tests for endpoints (`@WebMvcTest`) and queries (`@DataJpaTest`).
- Component tests for React UI (React Testing Library).
- Verify test coverage meets the team's standards.

### Step 5: Self-Review

Before presenting the implementation, review it against:
- The coding standards in `.github/instructions/`.
- The acceptance criteria from Step 1.
- The `code-review` skill's checklist (load it if available).

## Resources

- [Story Analysis Template](story-analysis-template.md)
