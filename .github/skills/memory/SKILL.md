---
name: memory
description: 'Project domain knowledge — entities, business rules, and integration contracts. Use this skill when the task involves understanding business logic, entity relationships, domain terminology, validation rules, event contracts, or when generating code that interacts with core domain models.'
---

# Project Domain Knowledge

This skill is the project's **structured memory** — the single source of truth for domain knowledge that the AI needs to generate correct, standards-compliant code. It covers what entities exist, what rules govern them, and how services communicate.

## When to Use This Skill

Loads this skill automatically when a task involves:
- Understanding or generating domain entities, fields, or relationships
- Enforcing business rules, invariants, or validation logic
- Working with inter-service events, API contracts, or integration boundaries
- Using domain-specific terminology in code, comments, or API naming

## How to Use This Skill

Read the files in this order — each layer builds on the previous:

1. **[Domain Model](domain-model.md)** — Entities, fields, relationships, aggregate boundaries, and lifecycle state machines. Start here to understand *what* the system models.
2. **[Business Rules](business-rules.md)** — Invariants, validations, policies, and terminology. Read this to understand *what constraints* govern the entities.
3. **[Integrations](integrations.md)** — Events published/consumed, external service contracts, and anti-corruption rules. Read this to understand *how services communicate*.

Use this knowledge to ensure generated code:
- Uses correct entity names, field types, and relationships
- Enforces all documented invariants and validations
- Respects service boundaries and anti-corruption rules
- Uses the ubiquitous language from the terminology glossary
- Handles state transitions only via documented valid paths

## Quick Reference

| File | Contains | When to check |
|------|----------|---------------|
| [domain-model.md](domain-model.md) | Entities, fields, aggregates, lifecycle states | Creating/modifying entities, writing queries, defining API shapes |
| [business-rules.md](business-rules.md) | Invariants, validations, policies, terminology | Writing service logic, validation, error handling |
| [integrations.md](integrations.md) | Events, external APIs, anti-corruption rules | Working with Kafka, calling external services, crossing context boundaries |

## Samples

The [samples/](samples/) subfolder contains fully filled examples using a fictional e-commerce domain. Use these as a reference for how to populate the template files above.

---

## Memory Maintenance Protocol

> **ASSUME THESE FILES MAY BE INCOMPLETE OR STALE.**
> Domain knowledge evolves as the project grows. These files are living documents.

When working on a task that involves this skill:

1. **Read first** — Always read the relevant memory files before generating code.
2. **Detect gaps** — If you discover a domain fact during implementation that is **not documented** in these files (a new entity, an undocumented business rule, a missing event, an unnamed integration), flag it.
3. **Propose updates** — Present the update as a concrete diff/suggestion to the developer for approval. Do not silently modify memory files.
4. **Format for proposed updates**:
   ```
   📝 Memory Update Suggested — [filename]
   Section: [section name]
   Add: [what to add, in the file's existing format]
   Reason: [why this was discovered / what task revealed it]
   ```
5. **Keep it organized** — When proposing updates, merge with existing entries rather than duplicating. Remove stale entries if you can confirm they're outdated.

### Freshness Indicators

Add a last-updated comment at the top of each file when making changes:
```markdown
<!-- Last updated: YYYY-MM-DD by [name/team] — [brief reason] -->
```
