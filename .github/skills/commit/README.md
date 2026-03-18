# /commit Skill

Commit staged and unstaged changes with atomic analysis and generated commit messages — without leaving your editor.

---

## Value

| Pain Point | What This Skill Does |
|---|---|
| Writing commit messages mid-flow | Auto-generates a message in the team's standard format (`#STORY [Author(s)] description`) from the actual diff |
| Lumped, hard-to-review commits | Analyses changes for atomicity and proposes a clean breakdown into logical commits for your approval before executing |
| Misconfigured git identity going unnoticed | Validates that the author email ends with `@JohnDeere.com` before a single commit is made |
| Forgetting to link a story | Prompts you to supply a story number or confirm `#nocard` if neither was provided at invocation |

---

## Prerequisites

| Requirement | Details |
|---|---|
| **Git** | `git` must be installed and the working directory must be inside a git repository |
| **Configured author identity** | `git config user.email` must be set to a `@JohnDeere.com` address |
| **At least one change** | The repository must have staged or unstaged changes — otherwise there is nothing to commit |

---

## How to Use

Trigger the skill in Copilot Chat:

```
/commit
```

```
commit my changes
```

```
stage and commit everything
```

Provide a story reference and credit your collaborators at invocation:

```
/commit story=DIAG-000 authors=Prajwal
```

```
/commit story=EPPT-3927 authors=Johny, Prashant
```

```
/commit nocard authors=Palani
```

All parameters are optional — if `story=` or `nocard` is omitted the skill will ask before proceeding.

---

## What Happens Under the Hood

1. **Stages all changes** via `git add -A` so nothing is missed.
2. **Validates the author email** from `git config` against the `@JohnDeere.com` domain — halts if invalid.
3. **Resolves the story reference** — uses the supplied `story=` key or `nocard`; if neither was provided, pauses and asks you to choose before continuing.
4. **Analyses atomicity** — determines whether the diff represents one logical unit of work or several.
5. **Single change** — generates one commit message and executes immediately, no approval needed.
6. **Multiple changes** — proposes a numbered list of atomic commits for your approval; executes all in sequence on yes, or falls back to a single commit on no.

---

## Commit Message Format

```
#<STORY-NUM|nocard> [<Author(s)>] <short description>
```

| Segment | Details |
|---|---|
| `#STORY-NUM` | Jira / issue key, e.g. `#DIAG-000`, `#EPPT-3927` |
| `#nocard` | Used when the commit is not linked to a story |
| `[Author(s)]` | Comma-separated names in square brackets — included only when `authors=` is provided |
| `description` | Imperative mood, sentence case, no trailing period, max 72 chars |

---

## Pre-commit Checks Summary

| Check | Behaviour on Failure |
|---|---|
| Author email (`@JohnDeere.com`) | Halt. Warn developer. Do not commit. |
| No changes present | Exit with "Nothing to commit" message. |
| Story reference missing | Pause and ask: supply a story number or confirm `#nocard`. |
| Developer rejects atomic split | Fall back to a single commit covering all changes. |

---

## Example Output

```
Proposed atomic commits:
  [1] #EPPT-3927 [Johny, Prashant] Extend pre-merge checks with terragrunt linting
  [2] #EPPT-3927 [Johny, Prashant] Extend pre-merge checks with yaml linting

Proceed with all 2? (yes / no — commit as one)
```

```
✔ #EPPT-3927 [Johny, Prashant] Extend pre-merge checks with terragrunt linting
✔ #EPPT-3927 [Johny, Prashant] Extend pre-merge checks with yaml linting

2 commits created on branch feat/EPPT-3927-pre-merge-checks
```