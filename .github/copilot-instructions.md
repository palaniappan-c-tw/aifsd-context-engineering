# Copilot Instructions — Project-Wide

> **Scope**: Always loaded into every Copilot interaction (Chat, Agent, Coding Agent, CLI).
> Keep this file thin. Only universal conventions that apply to every single interaction belong here

> Language & Path specific standards or rules → `.github/instructions/*.instructions.md`

> Domain knowledge (entities, rules, integrations) → `.github/memory/MEMORY.md`

> On Demand task workflows → `.github/skills/*/SKILL.md`

---

## Project Overview

<!-- One-paragraph description of what this application does, its purpose, and its users. -->

## Tech Stack

<!-- Brief listing only — no detailed rules here. Detailed coding standards live in .github/instructions/*.instructions.md -->

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Backend   | <!-- e.g., Java 21, Spring Boot 3.x -->  |
| Frontend  | <!-- e.g., React 18+, TypeScript -->     |
| Database  | <!-- e.g., PostgreSQL, Flyway -->        |
| Messaging | <!-- e.g., Kafka -->                     |
| Build     | <!-- e.g., Maven, npm -->                |
| CI/CD     | <!-- e.g., GitHub Actions -->            |

## Architecture

<!-- High-level architecture style (e.g., layered monolith, microservices, modular monolith).
     Include a module map table showing key modules, their locations, responsibilities, and boundaries.
     Describe the primary data flow (request → response path). -->

### Layering Rules

<!-- e.g., Controller → Service → Repository only. No skipping layers. -->

## Build & Run

<!-- Commands that Copilot (especially the Coding Agent) needs to build, test, lint, and run the project locally. -->

```shell
# Build
# TODO: Add build command

# Test
# TODO: Add test command

# Lint
# TODO: Add lint command

# Run locally
# TODO: Add local run command
```

## Security Non-Negotiables

<!-- Rules that apply regardless of language or framework. Examples:
     - Never log passwords, tokens, card numbers, or PII
     - Never commit secrets — use environment variables or a secrets manager
     - All endpoints require authentication unless explicitly marked public -->

## Conventions

<!-- Universal conventions that span the entire codebase. -->

### Git & Version Control

<!-- Branching model, branch naming, commit message format -->

### Pull Requests

<!-- PR title format, required reviewers, CI checks that must pass -->

### Code Organization

<!-- Top-level directory layout expectations, where new modules go -->
