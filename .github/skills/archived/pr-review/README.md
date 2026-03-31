# PR-REVIEW Skill

Comprehensive pull request review skill orchestrating code quality, test coverage, error handling, and type design analysis.

---

## Value

| Pain Point | What This Skill Does |
|---|---|
| Manually reviewing PRs across multiple dimensions | Spawns specialised subagents in parallel for code quality, test coverage, silent failures, and type design — giving you a multi-perspective review in one pass |
| Overlooking silent failures and swallowed exceptions | Runs a dedicated `silent-failure-hunter` subagent that scans for missing error logging, empty catch blocks, and inadequate error handling |
| Unclear whether tests cover the changed code | Runs a `pr-test-analyzer` subagent that cross-references source changes against their tests and rates coverage gaps |
| Forgetting to build before merging | Automatically detects the build tool (Gradle or Maven) and runs the build concurrently with the review |
| Inconsistent review standards | Resolves project instruction files (`.github/instructions/`) for every changed file and passes them to each subagent, ensuring reviews align with project conventions |

---

## Prerequisites

| Requirement | Details |
|---|---|
| **Git** | `git` must be installed and the working directory must be inside a git repository |
| **Base branch** | A base branch (default `main`) must exist for diff comparison |
| **Build tool** (optional) | `./gradlew` or `./mvnw` for the automated build step — if neither exists, the build step is skipped |
| **Instruction files** (optional) | `.github/instructions/*.instructions.md` for project-specific review rules — if missing, gaps are noted in the output |

---

## How to Use

Trigger the skill in Copilot Chat:

```
review my PR
```

```
review this pull request
```

```
is this ready to merge
```

```
review changes on this branch for merging
```

Optionally, scope the review or override defaults:

```
review my PR code tests
```

```
review my PR branch:develop
```

```
review my PR maven
```

```
review my PR tests errors branch:develop
```

---

## Arguments

| Argument | Effect |
|---|---|
| *(none)* | Run all applicable subagents, auto-detect build tool |
| `code` | Run `code-reviewer` only |
| `tests` | Run `pr-test-analyzer` only |
| `errors` | Run `silent-failure-hunter` only |
| `types` | Run `type-design-analyzer` only (if activation condition is met) |
| `all` | Run all subagents |
| `maven` | Override build tool preference to Maven |
| `branch:<name>` | Use `<name>` as the base branch instead of `main` |

Multiple arguments can be combined: e.g. `tests errors branch:develop`.

---

## What Happens Under the Hood

1. **Collects the diff** against the base branch and identifies all changed files.
2. **Detects large diffs** (500+ lines) and switches to a layered processing order: `domain/model → service → controller → repository → test`.
3. **Resolves instruction files** from `.github/instructions/` for each changed file path, mapping file patterns to the correct instruction files.
4. **Determines subagent activation** — `code-reviewer`, `pr-test-analyzer`, and `silent-failure-hunter` always run; `type-design-analyzer` activates only when entity/model/domain/dto/vo files are in the diff.
5. **Spawns subagents in parallel**, each scoped to the relevant files and instruction references.
6. **Runs the build concurrently** — auto-detects Gradle or Maven and executes compilation and tests.
7. **Aggregates results** from all subagents and the build step into a unified report.

---

## Subagents

| Subagent | Scope | Always Active |
|---|---|---|
| `code-reviewer` | All changed files | Yes |
| `pr-test-analyzer` | All changed files (cross-references source ↔ tests) | Yes |
| `silent-failure-hunter` | All changed source files excluding migration/SQL/config | Yes |
| `type-design-analyzer` | Only entity, model, domain, dto, vo files | No — only when matching files are in the diff |

---

## Output Format

### Violations

Findings grouped by severity:

- **Critical (must fix before merge)** — code issues scored 90–100, coverage gaps rated 9–10, critical silent failures, type design scores 1–3
- **Important (should fix)** — code issues scored 80–89, coverage gaps rated 7–8, high silent failures, type design scores 4–5
- **Suggestions (nice to have)** — all lower-severity findings

### Instruction File Gaps

Any expected instruction files not found on disk.

### Build

Pass or Fail with details on compilation errors or test failures.

### Verdict

`Approved` or `Changes Required` with a one-sentence reason.

---

## Example Output

```
### Violations

**Critical (must fix before merge)**
- [UserService.java:42] — Unchecked null return — java.instructions.md — [subagent: code-reviewer] — 95
- [UserService.java:42] — Exception swallowed silently — java.instructions.md — [subagent: silent-failure-hunter] — CRITICAL

**Important (should fix)**
- [UserServiceTest.java] — Missing edge-case test for null input — test.instructions.md — [subagent: pr-test-analyzer] — 8

**Suggestions (nice to have)**
- [UserDto.java:15] — Consider making field final — java.instructions.md — [subagent: type-design-analyzer] — 6

### Instruction File Gaps

(none)

### Build

Pass

### Verdict

Changes Required
Two critical findings must be addressed before merge.
```
