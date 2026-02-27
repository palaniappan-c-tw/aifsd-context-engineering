# AIFSD — AI-Friendly Software Development Context Template

A reference repository that defines a structured approach to **context engineering for GitHub Copilot**, reducing AI hallucinations and manual corrections when generating code from user stories.

## The Problem

Developers experience too much back-and-forth with AI code generation tools because the AI lacks knowledge of internal tech stacks, coding standards, and architectural patterns. This leads to hallucinated patterns, standards violations, and excessive manual correction.

## The Solution: Three-Tier Context Architecture

This repo organizes project context into three tiers, each mapped to a native GitHub Copilot feature:

```
┌─────────────────────────────────────────────────────────────────┐
│  Tier 1: Always-On (copilot-instructions.md)                    │
│  Loaded into EVERY Copilot interaction.                         │
│  Project identity, architecture, build commands, non-negotiables│
│  Thin by design.                                                │
├─────────────────────────────────────────────────────────────────┤
│  Tier 2: Auto by File Type (*.instructions.md)                  │
│  Loaded when matching files are open/edited.                    │
│  Language & framework coding standards, naming conventions.     │
│  As detailed as needed per language.                            │
├─────────────────────────────────────────────────────────────────┤
│  Tier 3: On-Demand Skills (skills/*/SKILL.md)                   │
│  Loaded when the AI determines the task is relevant.            │
│  Domain knowledge, step-by-step workflows, scripts, templates.  │
│  Progressive: metadata → instructions → resources.              │
└─────────────────────────────────────────────────────────────────┘
```

**Design principle**: Not too little, not too much. Each tier loads only when needed, keeping the AI's context window efficient.

## Structure

```
.github/
├── copilot-instructions.md              # Tier 1: Always-on project memory
│
├── instructions/                        # Tier 2: Path-scoped coding standards
│   ├── java.instructions.md             #   applyTo: **/*.java — Spring Boot conventions
│   ├── test.instructions.md             #   applyTo: **/*Test.java, *IT.java — testing standards
│   └── database.instructions.md         #   applyTo: **/db/migration/**, *.sql — Flyway & schema rules
│
└── skills/                              # Tier 3: On-demand capabilities
    ├── memory/                          #   Domain knowledge (entities, bounded contexts)
    │   ├── SKILL.md
    │   ├── entity-glossary.md
    │   └── bounded-contexts.md
    ├── story-to-code/                   #   Workflow: Jira story → implementation
    │   ├── SKILL.md
    │   └── story-analysis-template.md
    └── code-review/                     #   Workflow: structured code review
        ├── SKILL.md
        └── review-checklist.md
```

## What Goes Where

| Question | Answer | File |
|----------|--------|------|
| Should the AI **always** know this? | Project shape, build commands, global conventions | `copilot-instructions.md` |
| Should the AI know this **when touching specific file types**? | Language coding standards, framework patterns | `instructions/*.instructions.md` |
| Should the AI know this **when doing a specific task**? | Domain context, step-by-step workflows, templates | `skills/*/SKILL.md` |

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

### 4. Customize the skills

- **`memory/`** — Fill in your project's domain entities, bounded contexts, and business rules.
- **`story-to-code/`** — Adjust the workflow steps to match your team's development process.
- **`code-review/`** — Update the checklist to reflect your team's priorities.
- Add new skills for workflows specific to your project.

### 5. Commit and iterate

Commit the `.github/` folder to your repo. Context engineering is iterative — start with the basics and refine as you observe Copilot's behaviour.

## How to Maintain

- **Add rules reactively**: When Copilot generates code that violates a standard, add that rule to the relevant instruction file.
- **Keep `copilot-instructions.md` thin**: If it grows past ~2 pages, move content to instruction files or skills.
- **Update domain knowledge**: When entities, services, or boundaries change, update the `memory` skill.
- **Review quarterly**: Check if rules are still relevant — models improve over time and may no longer need certain guardrails.

## Compatibility

These context files work across all GitHub Copilot modes:

| Mode | `copilot-instructions.md` | `*.instructions.md` | Skills (`SKILL.md`) |
|------|:---:|:---:|:---:|
| Copilot Chat (Ask/Edit) | ✅ | ✅ | ✅ |
| Agent Mode (VS Code) | ✅ | ✅ | ✅ |
| Coding Agent (Cloud) | ✅ | ✅ | ✅ |
| GitHub CLI | ✅ | ✅ | ✅ |

## References

- [Context Engineering for Coding Agents — Martin Fowler / Birgitta Böckeler](https://martinfowler.com/articles/exploring-gen-ai/context-engineering-coding-agents.html)
- [GitHub Copilot Custom Instructions](https://docs.github.com/en/copilot/customizing-copilot/adding-repository-custom-instructions-for-github-copilot)
- [GitHub Copilot Agent Skills](https://docs.github.com/en/copilot/how-tos/use-copilot-agents/coding-agent/create-skills)
- [VS Code Copilot Customization](https://code.visualstudio.com/docs/copilot/copilot-customization)
