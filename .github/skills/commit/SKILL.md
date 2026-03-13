---
name: commit
description: >
  Creates well-formed git commits from staged and unstaged changes.
  Use when the developer says "commit my changes", "stage and commit",
  "create a git commit", "/commit", or asks to commit work in progress.
  Analyses atomicity, generates Conventional Commits messages, enforces
  @JohnDeere.com author identity, and supports pair-programming co-authorship.
  Do NOT trigger for queries about viewing diffs, git status, or git history.
---

# Overview

This skill produces clean, well-structured git commits by following a six-step workflow:

1. **Stage & collect** — runs `git add -A` and captures the full diff
2. **Validate identities** — enforces `@JohnDeere.com` email for the author and any co-author before proceeding
3. **Analyse atomicity** — determines whether the diff represents a single logical change or multiple unrelated concerns
4. **Commit (single)** — if atomic, generates and executes one commit immediately with no approval needed
5. **Commit (split)** — if non-atomic, proposes a numbered list of atomic commits for the developer to confirm; falls back to a single commit on rejection
6. **Format & trailers** — all messages follow Conventional Commits (`<type>(<scope>): <description>`), with an optional `Co-authored-by` trailer appended to every commit when a co-author is provided

## Parameters

The skill accepts one optional parameter:

```
/commit CoAuthor = <name> [<email@JohnDeere.com>]
```

- `CoAuthor = Sayeed` — name only, no email required
- `CoAuthor = Sayeed sayeed@JohnDeere.com` — name and email

If no `CoAuthor` is provided, proceed without a co-author trailer.

---

## Workflow

### Step 1 — Stage and collect

Run `git add -A` then `git diff HEAD` to capture the full diff.
If there are no changes, stop and tell the developer there is nothing to commit.

### Step 2 — Validate identities

**CRITICAL: Do not proceed past this step until both checks pass.**

1. Read `git config user.email`. It must end with `@JohnDeere.com`. If it does not, halt and tell the developer which email is invalid.
2. If a `CoAuthor` email was provided, it must also end with `@JohnDeere.com`. If it does not, halt and name the invalid email.

Name-only co-authors (no email provided) skip the email check entirely.

### Step 3 — Analyse atomicity

Read the full diff and answer: do all the changes serve a single, clearly stateable purpose?

Treat the changes as **non-atomic** if they span unrelated concerns — for example a bug fix alongside a new feature, production code alongside separable test additions, or a dependency update alongside feature code. See `references/atomicity-guide.md` for detailed criteria.

### Step 4a — Single logical change

Generate one commit message and execute immediately. No developer approval needed.

### Step 4b — Multiple logical changes

Break the diff into atomic units (choose the granularity — file-level, hunk-level, or logical grouping — that best represents the actual separation). Present a numbered list:

```
Proposed atomic commits:
  [1] feat(auth): add JWT middleware
  [2] test(auth): add unit tests for JWT
  [3] chore(deps): add jsonwebtoken package

Proceed with all 3? (yes / no — commit as one)
```

- **Yes** → execute all commits in sequence automatically
- **No** → fall back to a single commit covering all changes (run Step 4a)

### Step 5 — Commit message format

All messages must follow Conventional Commits. See `references/commit-standards.md` for the full type list and examples.

```
<type>(<scope>): <short description>
```

- `scope` is derived from the most affected module, directory, or component
- `short description` is imperative mood, lowercase, no trailing period, max 72 chars
- Add a body only when the change needs additional context (e.g. breaking changes)

### Step 6 — Append co-author trailer

If a co-author was provided, append to every commit message:

```
Co-authored-by: Sayeed <sayeed@JohnDeere.com>   # name + email
Co-authored-by: Sayeed <>                         # name only
```

---

## Important

- Always run `git add -A` first — never commit only staged files. The full diff is required for accurate atomicity analysis.
- Never commit if either identity check fails. Warn clearly and stop.
- Never ask the developer to choose a commit message — generate it from the diff.
- If the developer rejects the atomic split, always fall back to a single commit. Do not abort or ask again.