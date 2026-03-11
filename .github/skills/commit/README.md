# /commit Skill

Commit staged and unstaged changes with atomic analysis, generated commit messages, and co-author support — without leaving your editor.

---

## Value

| Pain Point | What This Skill Does |
|---|---|
| Writing commit messages mid-flow | Auto-generates a **Conventional Commits** message (`type(scope): description`) from the actual diff  |
| Lumped, hard-to-review commits | Analyses changes for atomicity and proposes a clean breakdown into logical commits for your approval before executing |
| Forgetting pair partner | Accepts a `CoAuthor` parameter and appends the `Co-authored-by` git trailer to every commit automatically |
| Misconfigured git identity going unnoticed | Validates that both the author and co-author emails end with `@JohnDeere.com` before a single commit is made |
| Manually staging files before committing | Auto-stages all changes (staged + unstaged) so the AI sees the full picture and you skip the `git add` step |

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

Optionally, credit your pair partner at invocation:

```
/commit CoAuthor = Sayeed
```

```
/commit CoAuthor = Sayeed sayeed@JohnDeere.com
```

---

## What Happens Under the Hood

1. **Stages all changes** via `git add -A` so nothing is missed.
2. **Validates the author email** from `git config` against the `@JohnDeere.com` domain — halts if invalid.
3. **Validates the co-author email** if one was provided — halts and names the offending address if invalid.
4. **Analyses atomicity** — determines whether the diff represents one logical unit of work or several.
5. **Single change** — generates one Conventional Commits message and executes immediately, no approval needed.
6. **Multiple changes** — proposes a numbered list of atomic commits for your approval; executes all in sequence on yes, or falls back to a single commit on no.
7. **Appends co-author trailer** to every commit if a co-author was provided.

---

## Pre-commit Checks Summary

| Check | Behaviour on Failure |
|---|---|
| Author email (`@JohnDeere.com`) | Halt. Warn developer. Do not commit. |
| Co-author email (`@JohnDeere.com`) | Halt. Name the invalid email. Do not commit. |
| No changes present | Exit with "Nothing to commit" message. |
| Developer rejects atomic split | Fall back to a single commit covering all changes. |

---

## Example Output

```
Proposed atomic commits:
  [1] feat(auth): add JWT middleware
  [2] test(auth): add unit tests for JWT
  [3] chore(deps): add jsonwebtoken package

Proceed with all 3? (yes / no — commit as one)
```

```
✔ feat(auth): add JWT middleware
✔ test(auth): add unit tests for JWT
✔ chore(deps): add jsonwebtoken package

3 commits created on branch feat/AUTH-42-jwt-auth
Co-authored-by: Sayeed <sayeed@JohnDeere.com>
```