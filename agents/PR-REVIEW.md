# PR-REVIEW AGENT

Runs a multi-perspective review of your pull request — code quality, test coverage, silent failures, and type design — in a single pass.

---

## Value

| Pain Point | What This Agent Does |
|---|---|
| Manually reviewing PRs across multiple dimensions | Spawns specialised subagents in parallel for code quality, test coverage, silent failures, and type design — giving you a multi-perspective review in one pass |
| Forgetting to build before merging | Automatically detects the build tool (Gradle or Maven) and runs the build concurrently with the review |
| Inconsistent review standards | Resolves project instruction files (`.github/instructions/`) for every changed file and passes them to each subagent, ensuring reviews align with project conventions |

---

## Prerequisites
- **Git**: `git` must be installed and the working directory must be inside a git repository
- **Build tool** (optional): `./gradlew` or `./mvnw` for the automated build step — if neither exists, the build step is skipped

---

## How to Use

In VS Code, open **Copilot Chat** and switch to the `pr-reviewer` agent. You can do this by:

- Selecting `pr-reviewer` from the agent/mode picker dropdown in Chat, **or**
- Typing `/agents` in the Chat input and selecting `pr-reviewer`

---

## What Happens Under the Hood

1. **Auto-detects the base branch** — checks for `main` first, falls back to `master`. Halts with a clear message if neither exists. Runs `git diff <base>...HEAD --name-only` to collect changed file paths and captures the full diff content to pass to subagents.
2. **Detects large diffs** (500+ lines) and switches to a layered processing order: `domain/model → service → controller → repository → test`. This ordering is passed to all subagents.
3. **Determines subagent activation** — `pr-code-reviewer`, `pr-test-analyzer`, and `pr-silent-failure-hunter` always run. `pr-type-design-analyzer` activates only when the diff contains files matching path patterns: `**/entity/**`, `**/model/**`, `**/domain/**`, `**/dto/**`, `**/vo/**`, `**/valueobject/**`.

4. **Spawns subagents in parallel**, each receiving a scoped slice of the diff plus the large-diff flag and layer processing order when applicable:
   - `pr-code-reviewer` — full git diff
   - `pr-test-analyzer` — full git diff
   - `pr-silent-failure-hunter` — git diff excluding migration/SQL/config hunks
   - `pr-type-design-analyzer` — diff scoped to the files that triggered its activation only

5. **Runs the build concurrently** — detection order: `./gradlew build` first, then `./mvnw compile test`. If both exist, Gradle is preferred. If neither exists, the build step is skipped.

6. **Aggregates results** from all subagents and the build step into a unified report. If a subagent fails to return results or errors out, the gap is noted under a `### Subagent Failures` section — a single agent failure never blocks the entire review.

7. **Automatically invokes the `troubleshoot` skill** after the agent finishes (whether successfully or with errors). Analyses the session's debug logs and produces a structured Agent Debug Report saved to `.github/agent-logs/agent-debug-[timestamp].md`. The report includes: session summary, execution trace table, and errors & warnings.

---

## Subagents

| Subagent | Scope | Always Active | Tools | Also User-Invocable |
|---|---|---|---|---|
| `pr-code-reviewer` | Full git diff — self-resolves the instruction file map from `.github/instructions/`, reads matched instruction files, then reviews code quality, style, naming, layering, security, and compliance. Only reports issues with confidence ≥ 80 (scale 0–100). | Yes | `read`, `search` | Yes — for standalone pre-commit checks |
| `pr-test-analyzer` | Full git diff (cross-references source ↔ tests) — checks behavioural coverage, edge cases, assertion quality (DAMP), and test resilience. Rates gaps 1–10. | Yes | `read`, `search` | Yes |
| `pr-silent-failure-hunter` | Git diff excluding migration/SQL/config hunks — hunts for empty catch blocks, swallowed exceptions, missing error logging, unjustified fallbacks, broad exception catches. Rates CRITICAL/HIGH/MEDIUM. | Yes | `read`, `search` | Yes |
| `pr-type-design-analyzer` | Diff scoped to entity, model, domain, dto, vo files only — rates encapsulation, invariant expression, invariant usefulness, and invariant enforcement (each 1–10). Never reviews SQL or migration files. | No — only when matching path patterns are in the diff | `read`, `search` | Yes |