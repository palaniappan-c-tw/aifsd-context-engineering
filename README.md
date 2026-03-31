# AIFSD — AI-Friendly Software Development Context Template

A reference repository that defines a structured approach to **context engineering for GitHub Copilot**, reducing AI hallucinations and manual corrections when generating code from user stories.

## The Problem

Developers experience too much back-and-forth with AI code generation tools because the AI lacks knowledge of internal tech stacks, coding standards, and architectural patterns. This leads to hallucinated patterns, standards violations, and excessive manual correction.

## The Solution: Layered Context Architecture

This repo organizes project context into distinct layers, each mapped to a native GitHub Copilot feature:

```
┌─────────────────────────────────────────────────────────────────┐
│  Always-On (copilot-instructions.md)                          │
│  Loaded into EVERY Copilot interaction.                       │
│  Project identity, architecture, build commands, non-negotiables│
│  Thin by design.                                              │
├─────────────────────────────────────────────────────────────────┤
│  Auto by File Type (*.instructions.md)                        │
│  Loaded when matching files are open/edited.                  │
│  Language & framework coding standards, naming conventions.   │
│  As detailed as needed per language.                          │
├─────────────────────────────────────────────────────────────────┤
│  Domain Knowledge (memory/)                                   │
│  Pure content: entities, business rules, integration contracts│
│  Loaded on demand by skills that need domain context.         │
│  Easy to read and edit.                                       │
├─────────────────────────────────────────────────────────────────┤
│  On-Demand Skills (skills/*/SKILL.md)                         │
│  Loaded when the AI determines the task is relevant.          │
│  Step-by-step workflows that orchestrate domain knowledge.    │
│  Progressive: metadata → instructions → resources.            │
├─────────────────────────────────────────────────────────────────┤
│  Custom Agents (agents/*.md)                                  │
│  Reusable personas with dedicated tools and instructions.     │
│  Invoked as subagents by skills or directly by the user.      │
│  Each agent owns a single concern (review, security, tests).  │
└─────────────────────────────────────────────────────────────────┘
```

**Design principle**: Memory owns the **content** (pure domain knowledge), skills own the **orchestration** (when and which memory files to pull in), and agents own **specialised personas** that skills can delegate to. Each layer loads only when needed, keeping the AI's context window efficient.

## Structure

```
.github/
├── copilot-instructions.md              #   Always-on project memory
│
├── instructions/                        #   Path-scoped coding standards
│   ├── java.instructions.md             #   applyTo: **/*.java — Spring Boot conventions
│   ├── test.instructions.md             #   applyTo: **/*Test.java, *IT.java — testing standards
│   └── database.instructions.md         #   applyTo: **/db/migration/**, *.sql — Flyway & schema rules
│
├── agents/                              #   Custom agent personas
│   ├── code-reviewer.md                 #   Lightweight code quality reviewer
│   ├── pr-test-analyzer.md              #   Test coverage & quality analyzer
│   ├── silent-failure-hunter.md         #   Detects swallowed exceptions & missing error handling
│   ├── type-design-analyzer.md          #   Reviews type/model design quality
│   ├── security-reviewer.md             #   End-to-end security audit orchestrator (Burp MCP)
│   └── security-review-instructions/    #   Subagent instructions for security checks
│       ├── recon.md, headers.md, fingerprint.md
│       ├── cors.md, cookie-flags.md, cookie-expiry.md
│       ├── https.md, idor.md, sqli-json.md
│       └── numeric-boundary.md
│
├── memory/                              #   Domain knowledge
│   ├── MEMORY.md                        #   Index + memory maintenance protocol
│   ├── README.md                        #   Explains the concept and approach for developers
│   ├── domain-model.md                  #   Entities, aggregates, lifecycle states
│   ├── business-rules.md                #   Invariants, validations, policies, terminology
│   ├── integrations.md                  #   Events, external APIs, anti-corruption rules
│   └── samples/                         #   Filled e-commerce examples for reference
│       ├── domain-model-sample.md
│       ├── business-rules-sample.md
│       └── integrations-sample.md
│
└── skills/                              #   On-demand workflows
    ├── commit/                          #   Workflow: stage, analyse, and commit changes
    │   ├── SKILL.md
    │   ├── README.md
    │   └── references/                  #   Atomicity guide & commit standards
    ├── pr-review/                       #   Workflow: parallel subagent PR review
    │   ├── SKILL.md
    │   └── README.md
    ├── pr-submit/                       #   Workflow: prepare and submit a GitHub PR
    │   ├── SKILL.md
    │   └── README.md
    └── archived/                        #   Retired skills kept for reference
        ├── story-to-code/
        ├── code-review/
        └── exception-handling/
```

## What Goes Where

| Question | Answer | File |
|----------|--------|------|
| Should the AI **always** know this? | Project shape, build commands, global conventions | `copilot-instructions.md` |
| Should the AI know this **when touching specific file types**? | Language coding standards, framework patterns | `instructions/*.instructions.md` |
| Should the AI know this **when doing a specific task**? | Step-by-step workflows, templates | `skills/*/SKILL.md` |
| Should the AI know this **when generating domain-aware code**? | Domain entities, rules, integration contracts | `memory/MEMORY.md` |
| Should the AI **act as a specialised persona**? | Focused reviewers, security auditors, test analyzers | `agents/*.md` |

## How to Adopt

### 1. Copy the `.github/` folder into your project

```shell
cp -r .github/ /path/to/your-project/.github/
```

### 2. Customize `copilot-instructions.md`

Fill in your project's specifics:
- Project name and one-paragraph description
- Tech stack table
- Architecture overview and module map
- Build, test, lint, and run commands
- Security non-negotiables

### 3. Customize the instruction files

Instruction files are scoped **by concern**, not by language. Each file targets a specific part of your codebase via its `applyTo` glob:
- `java.instructions.md` — Spring Boot application code conventions (applyTo: `**/*.java`)
- `test.instructions.md` — Testing standards (applyTo: `**/*Test.java`, `*IT.java`, etc.)
- `database.instructions.md` — Flyway migrations and schema rules (applyTo: `**/db/migration/**`, `*.sql`)

Add, remove, or replace files to match your stack. Examples:
- `react.instructions.md` with `applyTo: "**/*.tsx,**/*.jsx"` for React projects
- `python.instructions.md` with `applyTo: "**/*.py"` for Python projects
- `api.instructions.md` with `applyTo: "**/controller/**"` for API-specific conventions

### 4. Populate the memory files

Fill in the domain knowledge files under `memory/`:
- **`domain-model.md`** — Your project's entities, fields, aggregate boundaries, and lifecycle state machines.
- **`business-rules.md`** — Invariants, validations, policies, and domain terminology.
- **`integrations.md`** — Events published/consumed, external service contracts, and anti-corruption rules.

See the `samples/` subfolder for a complete e-commerce example. The AI maintenance protocol in `MEMORY.md` means the AI will also propose updates to these files as it discovers undocumented domain facts.

### 5. Customize the skills

- **`commit/`** — Adjust atomicity rules, commit message format, and author identity checks to match your team's standards.
- **`pr-review/`** — Configure which subagents run (code quality, tests, error handling, type design) and the base branch defaults.
- **`pr-submit/`** — Update PR title/description templates, required reviewers, and CI gate rules.
- Add new skills for workflows specific to your project.

All three active skills include an **On Completion** task that automatically invokes the `troubleshoot` skill to generate an Agent Debug Report after every run. Reports are saved to `.github/agent-logs/agent-debug-[YYYYMMDD-HHmmss].md` and surfaced inline in the chat.

### 6. Customize the agents

Agents are reusable personas that can be invoked as subagents by skills or directly by the user:
- **`code-reviewer.md`** — Lightweight single-pass code quality reviewer for pre-commit/pre-PR checks.
- **`pr-test-analyzer.md`** — Analyses test coverage, edge cases, and assertion quality.
- **`silent-failure-hunter.md`** — Hunts for swallowed exceptions and missing error handling.
- **`type-design-analyzer.md`** — Reviews type/model design for encapsulation and invariant enforcement.
- **`security-reviewer.md`** — End-to-end security audit orchestrator that uses Burp Suite MCP tools, spawns parallel subagents for passive and active security checks, and aggregates findings into a structured report.

The security reviewer includes its own `security-review-instructions/` subfolder with dedicated instruction files for each check type (recon, headers, CORS, cookies, IDOR, SQLi, etc.).

### 7. Commit and iterate

Commit the `.github/` folder to your repo. Context engineering is iterative — start with the basics and refine as you observe Copilot's behaviour.

## How to Maintain

- **Add rules reactively**: When Copilot generates code that violates a standard, add that rule to the relevant instruction file.
- **Keep `copilot-instructions.md` thin**: If it grows past ~2 pages, move content to instruction files or skills.
- **Update domain knowledge**: When entities, services, or boundaries change, update the files in `.github/memory/`. See [memory/README.md](.github/memory/README.md) for guidance.
- **Review quarterly**: Check if rules are still relevant — models improve over time and may no longer need certain guardrails.

## Compatibility

These context files work across all GitHub Copilot modes:

| Mode | `copilot-instructions.md` | `*.instructions.md` | Memory (`MEMORY.md`) | Skills (`SKILL.md`) | Agents (`agents/*.md`) |
|------|:---:|:---:|:---:|:---:|:---:|
| Copilot Chat (Ask/Edit) | ✅ | ✅ | ✅ | ✅ | ✅ |
| Agent Mode (VS Code) | ✅ | ✅ | ✅ | ✅ | ✅ |
| Coding Agent (Cloud) | ✅ | ✅ | ✅ | ✅ | ✅ |
| GitHub CLI | ✅ | ✅ | ✅ | ✅ | ✅ |

## References

- [Context Engineering for Coding Agents — Martin Fowler / Birgitta Böckeler](https://martinfowler.com/articles/exploring-gen-ai/context-engineering-coding-agents.html)
- [GitHub Copilot Custom Instructions](https://docs.github.com/en/copilot/customizing-copilot/adding-repository-custom-instructions-for-github-copilot)
- [GitHub Copilot Agent Skills](https://docs.github.com/en/copilot/how-tos/use-copilot-agents/coding-agent/create-skills)
- [VS Code Copilot Customization](https://code.visualstudio.com/docs/copilot/copilot-customization)
- [Effective context engineering for AI agents](https://www.anthropic.com/engineering/effective-context-engineering-for-ai-agents)