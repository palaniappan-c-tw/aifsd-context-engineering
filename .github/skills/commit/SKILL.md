---
name: commit
description: >
  Creates well-formed git commits from staged and unstaged changes.
  Use when the developer says "commit my changes", "stage and commit",
  "create a git commit", "/commit", or asks to commit work in progress.
  Analyses atomicity, generates commit messages in the team's standard format,
  and enforces @JohnDeere.com author identity.
  Do NOT trigger for queries about viewing diffs, git status, or git history.
---

# Overview

Produce clean, well-structured git commits by following a six-step workflow:

1. **Stage & collect** — runs `git add -A` and captures the full diff
2. **Validate identity** — enforces `@JohnDeere.com` email for the author before proceeding
3. **Resolve story reference** — determines the story number / `nocard` tag to use
4. **Analyse atomicity** — determines whether the diff represents a single logical change or multiple unrelated concerns
5. **Commit (single)** — if atomic, generates and executes one commit immediately with no approval needed
6. **Commit (split)** — if non-atomic, proposes a numbered list of atomic commits for the developer to confirm; falls back to a single commit on rejection

---

## Parameters

```
/commit [story=<BOARD-NUM> | nocard] [authors=<Name1>, <Name2>, ...]
```

| Parameter | Description |
|-----------|-------------|
| `story=DIAG-123` | Jira / issue reference to embed in the commit message |
| `nocard` | Explicitly marks the commit as having no linked story |
| `authors=Name1, Name2` | One or more developer names to place in square brackets. Names only — no emails required. |

**Examples**

```
/commit story=DIAG-000 authors=Prajwal
/commit story=EPPT-3927 authors=Johny, Prashant
/commit nocard authors=Palani
/commit authors=Ravi          ← story reference not provided; see Step 3
```

---

## Workflow

### Step 1 — Stage and collect

Run `git add -A` then `git diff HEAD` to capture the full diff.
If there are no changes, stop and tell the developer there is nothing to commit.

---

### Step 2 — Validate author identity

**CRITICAL: Do not proceed past this step until this check passes.**

Read `git config user.email`. It must end with `@JohnDeere.com`.
If it does not, halt immediately and tell the developer which email is invalid.

---

### Step 3 — Resolve story reference

If neither `story=` nor `nocard` was supplied, pause and ask:

```
No story number provided. How would you like to proceed?
  [1] Enter a story number (e.g. DIAG-123)
  [2] Continue with #nocard
```

- If the developer provides a story number, use `#<STORY-NUM>` as the prefix.
- If the developer chooses `nocard` (or types `2`), use `#nocard` as the prefix.
- Do not proceed until one of the two options is confirmed.

---

### Step 4 — Analyse atomicity

Read the full diff and answer: do all the changes serve a single, clearly stateable purpose?

Treat the changes as **non-atomic** if they span unrelated concerns — for example a bug fix alongside a new feature, production code alongside separable test additions, or a dependency update alongside feature code.

---

### Step 5a — Single logical change

Generate one commit message and execute immediately. No developer approval needed.

---

### Step 5b — Multiple logical changes

Break the diff into atomic units (choose the granularity — file-level, hunk-level, or logical grouping — that best represents the actual separation). Present a numbered list:

```
Proposed atomic commits:
  [1] #EPPT-3927 [Johny, Prashant] add JWT middleware
  [2] #EPPT-3927 [Johny, Prashant] add unit tests for JWT
  [3] #EPPT-3927 [Johny, Prashant] add jsonwebtoken package

Proceed with all 3? (yes / no — commit as one)
```

- **Yes** → execute all commits in sequence automatically
- **No** → fall back to a single commit covering all changes (run Step 5a)

---

### Step 6 — Commit message format

All messages must follow this exact structure:

```
#<STORY-NUM|nocard> [<Author(s)>] <short description>
```

| Segment | Rules |
|---------|-------|
| `#<STORY-NUM>` | Jira/issue key, e.g. `#DIAG-000`, `#EPPT-3927` |
| `#nocard` | Used when no story is linked |
| `[<Author(s)>]` | Comma-separated names in square brackets, e.g. `[Prajwal]` or `[Johny, Prashant]`. Omit if no `authors=` argument was given. |
| `<short description>` | Imperative mood, sentence case, no trailing period, max 72 chars total per line |

**Canonical examples**

```
#DIAG-000 [Prajwal] CVE-2025-12543 Trivy vulnerability fix
#EPPT-3927 [Johny, Prashant] Extend pre-merge checks with terragrunt and yaml linting
#nocard [Palani] Update docs
```

Add a **body** only when the change needs additional context (e.g. breaking changes or non-obvious reasoning). No `Co-authored-by` trailers are appended.

---

## Important

- Always run `git add -A` first — never commit only staged files. The full diff is required for accurate atomicity analysis.
- Never commit if the author identity check fails. Warn clearly and stop.
- Never ask the developer to choose a commit message — generate it from the diff.
- Always resolve the story reference (Step 3) before generating any commit message.
- If the developer rejects the atomic split, always fall back to a single commit. Do not abort or ask again.
- No `Co-authored-by` trailers are used under any circumstances.