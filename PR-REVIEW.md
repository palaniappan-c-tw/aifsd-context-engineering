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

## Arguments

| Argument | Effect |
|---|---|
| *(none)* | Run all applicable subagents, auto-detect build tool |
| `code` | Run `pr-code-reviewer` only |
| `tests` | Run `pr-test-analyzer` only |
| `errors` | Run `pr-silent-failure-hunter` only |
| `types` | Run `pr-type-design-analyzer` only (if activation condition is met) |
| `all` | Run all subagents |
| `maven` | Override build tool preference to Maven |
| `branch:<name>` | Use `<name>` as the base branch instead of `main` |

Multiple arguments can be combined: e.g. `tests errors branch:develop`.

---

## What Happens Under the Hood

### Phase 1 — Setup

1. **Collects the diff** against the base branch (`git diff <base>...HEAD --name-only`) and identifies all changed files. Then counts diff lines to detect large diffs.
2. **Detects large diffs** (500+ lines) and switches to a layered processing order: `domain/model → service → controller → repository → test`. This ordering is passed to all subagents.
3. **Builds the instruction file map** from `.github/instructions/` using this mapping:

   | Changed file pattern | Instruction file(s) resolved |
   |---|---|
   | `*.java` | `java.instructions.md` + `copilot-instructions.md` |
   | `*Test.java`, `*Tests.java`, `*Spec.java` | `test.instructions.md` + `copilot-instructions.md` |
   | `**/migration/**`, `**/flyway/**`, `**/liquibase/**`, `**/changelog/**`, `*.sql` | `database.instructions.md` |
   | Any unmatched extension | `copilot-instructions.md` |

   `copilot-instructions.md` is **always included** for every file regardless of extension. If a resolved instruction file does not exist on disk, it is skipped and the gap is recorded — never fails silently.

4. **Determines subagent activation** — `code-reviewer`, `pr-test-analyzer`, and `silent-failure-hunter` always run. `type-design-analyzer` activates only when the diff contains files matching **path patterns** (`**/entity/**`, `**/model/**`, `**/domain/**`, `**/dto/**`, `**/vo/**`, `**/valueobject/**`) or **content patterns** (`@Entity`, `@Embeddable`, `data class`, `@dataclass`, `BaseModel`).

### Phase 2 — Review (parallel) + Build (concurrent)

5. **Spawns subagents in parallel**, each receiving:
   - The list of changed file paths scoped to that agent's concern
   - The resolved instruction file paths (agents read these first before reviewing)
   - Whether large-diff mode is active and the layer processing order

6. **Runs the build concurrently** — detection order: `./gradlew build` first, then `./mvnw compile test`. If both exist, Gradle is preferred unless the `maven` argument is passed. If neither exists, the build step is skipped.

### Phase 3 — Aggregation

7. **Aggregates results** from all subagents and the build step into a unified report. If a subagent fails to return results or errors out, the gap is noted under a `### Subagent Failures` section — a single agent failure never blocks the entire review.

### Phase 4 — On Completion

8. **Automatically invokes the `troubleshoot` skill** after the agent finishes (whether successfully or with errors). Analyses the session's debug logs and produces a structured Agent Debug Report saved to `.github/agent-logs/agent-debug-[timestamp].md`. The report includes: session summary, execution trace table, and errors & warnings.

---

## Subagents

| Subagent | Scope | Always Active | Tools | Also User-Invocable |
|---|---|---|---|---|
| `pr-code-reviewer` | All changed files — reviews code quality, style, naming, layering, security, and instruction file compliance. Only reports issues with confidence ≥ 80 (scale 0–100). | Yes | `read`, `search` | Yes — for standalone pre-commit checks |
| `pr-test-analyzer` | All changed files (cross-references source ↔ tests) — checks behavioural coverage, edge cases, assertion quality (DAMP), and test resilience. Rates gaps 1–10. | Yes | `read`, `search` | Yes |
| `pr-silent-failure-hunter` | All changed source files excluding migration/SQL/config — hunts for empty catch blocks, swallowed exceptions, missing error logging, unjustified fallbacks, broad exception catches. Rates CRITICAL/HIGH/MEDIUM. | Yes | `read`, `search` | Yes |
| `pr-type-design-analyzer` | Only entity, model, domain, dto, vo files — rates encapsulation, invariant expression, invariant usefulness, and invariant enforcement (each 1–10). Never reviews SQL or migration files. | No — only when matching files are in the diff | `read`, `search` | Yes |