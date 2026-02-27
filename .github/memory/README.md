# Memory — Project Domain Knowledge

## What Is Memory?

Memory is a collection of plain Markdown files that capture **project-specific domain knowledge** — the entities, business rules, and integration contracts that define how the system works. This knowledge is stored outside the AI's context window and loaded on demand, giving the AI accurate, up-to-date facts about the domain without bloating every interaction.

Memory covers:
- **Domain Model** — What entities exist, their fields, relationships, aggregate boundaries, and lifecycle states.
- **Business Rules** — Invariants that must always hold, input validations, business policies, and the ubiquitous language (terminology glossary).
- **Integrations** — Events published and consumed, external service contracts, and anti-corruption rules that enforce service boundaries.

## Why a Separate Folder?

This project follows a **separation of concerns** between domain knowledge and AI workflows:

| Concern | Where it lives | What it does |
|---------|---------------|--------------|
| **Domain knowledge** (data) | `.github/memory/` | Pure content — entities, rules, contracts. Easy to read and edit by anyone. No skill machinery or YAML frontmatter. |
| **Workflows** (orchestration) | `.github/skills/` | Procedures — step-by-step instructions for tasks like story-to-code or code review. Skills decide *when and which* memory files to pull in. |

**Why not keep memory inside a skill?** A skill's job is to define a *procedure* — "when doing X, follow these steps." Domain knowledge has no procedure; it's reference data. Wrapping it in a skill adds unnecessary machinery (frontmatter, skill-matching heuristics) around content that should just be plain, editable Markdown.

This design is inspired by two patterns from Anthropic's context engineering research:

1. **Structured note-taking** ([Anthropic blog](https://www.anthropic.com/engineering/effective-context-engineering-for-ai-agents)) — Persist critical knowledge outside the context window so it survives across sessions. The AI reads its own notes to recover state, rather than relying on the context window alone.

2. **Just-in-time context** — Don't load everything upfront. Let the orchestration layer (skills) pull in exactly the data files needed for the current task. This keeps the AI's attention budget focused on what's relevant right now.

## How It Works

```
Developer asks: "Implement the order cancellation story"

  ┌──────────────────────────┐
  │  story-to-code (skill)   │   ← Orchestration: knows the workflow
  │  Step 1: "Read           │
  │  .github/memory/MEMORY.md│   ← Entry point: thin index of all memory files
  │  to find domain files"   │
  └──────────┬───────────────┘
             │ reads index
             ▼
  ┌──────────────────────────┐
  │  MEMORY.md               │   ← File lookup table: "domain-model.md for entities,
  │  (this folder)           │      business-rules.md for invariants, ..."
  └──────────┬───────────────┘
             │ reads relevant files
             ├──→ domain-model.md      (Order lifecycle, state transitions)
             ├──→ business-rules.md    (cancellation grace period, refund policy)
             └──→ integrations.md      (OrderCancelled event, stock release)
```

The AI reads **only the files relevant to the task**, not all of them. `MEMORY.md` serves as the single entry point that tells the AI what's available.

## How to Maintain

Memory files are **living documents** that evolve with the project. They should be updated:

| When | Action |
|------|--------|
| New entity or service added | Add to `domain-model.md` and `integrations.md` |
| Domain modelling session | Update all affected files |
| New business rule discovered | Add to `business-rules.md` |
| Post-incident reveals undocumented rule | Add to `business-rules.md` with context |
| Quarterly review | Scan for stale entries, remove deprecated items |

The AI also helps maintain memory — see the **Memory Maintenance Protocol** in [MEMORY.md](MEMORY.md). When the AI discovers an undocumented domain fact during a task, it proposes an update for developer approval.

## File Structure

```
.github/memory/
├── MEMORY.md                        # Index + maintenance protocol (read this first)
├── README.md                        # This file — explains the concept for developers
├── domain-model.md                  # Entities, aggregates, lifecycle state machines
├── business-rules.md                # Invariants, validations, policies, terminology
├── integrations.md                  # Events, external APIs, anti-corruption rules
└── samples/                         # Filled e-commerce examples for reference
    ├── domain-model-sample.md
    ├── business-rules-sample.md
    └── integrations-sample.md
```

## Samples

The [samples/](samples/) subfolder contains fully populated examples using a fictional e-commerce domain (Order, Customer, Product, Payment, Shipment). Use these as a reference when filling in the template files for your own project.
