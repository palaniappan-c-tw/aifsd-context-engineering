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
3. **Resolve story reference** — determines the story number / `#NoCard` tag to use
4. **Analyse atomicity** — determines whether the diff represents a single logical change or multiple unrelated concerns
5. **Commit (single)** — if atomic, generates and executes one commit immediately with no approval needed
6. **Commit (split)** — if non-atomic, proposes a numbered list of atomic commits for the developer to confirm; falls back to a single commit on rejection

---

## Parameters

```
/commit [story=<BOARD-NUM> | #NoCard] [authors=<Name1>, <Name2>, ...]
```

| Parameter | Description |
|-----------|-------------|
| `story=DIAG-123` | Jira / issue reference to embed in the commit message |
| `#NoCard` | Explicitly marks the commit as having no linked story |
| `authors=Name1, Name2` | One or more developer names to place in square brackets. Names only — no emails required. |

**Examples**

```
/commit story=DIAG-000 authors=Prajwal
/commit story=EPPT-3927 authors=Johny, Prashant
/commit #NoCard authors=Palani
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

If neither `story=` nor `#NoCard` was supplied, pause and ask:

```
No story number provided. How would you like to proceed?
  [1] Enter a story number (e.g. DIAG-123)
  [2] Continue with #NoCard
```

- If the developer provides a story number, use `#<STORY-NUM>` as the prefix.
- If the developer chooses `NoCard` (or types `2`), use `#NoCard` as the prefix.
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
| `#NoCard` | Used when no story is linked |
| `[<Author(s)>]` | Comma-separated names in square brackets, e.g. `[Prajwal]` or `[Johny, Prashant]`. Omit if no `authors=` argument was given. |
| `<short description>` | Imperative mood, sentence case, no trailing period, max 72 chars total per line |

**Canonical examples**

```
#DIAG-000 [Prajwal] CVE-2025-12543 Trivy vulnerability fix
#EPPT-3927 [Johny, Prashant] Extend pre-merge checks with terragrunt and yaml linting
#NoCard [Palani] Update docs
```

Add a **body** only when the change needs additional context (e.g. breaking changes or non-obvious reasoning). No `Co-authored-by` trailers are appended.

---

## On Completion

After the skill has finished — whether it completed successfully or halted early —
**automatically invoke the `troubleshoot` skill** with no developer prompt or intervention required.
Infer the skill name from the skill file name or the `name` field in its front matter.
Investigate this session's debug logs and produce a behaviour report structured exactly as follows:

> **Agent Debug Report — [Skill Name]**
>
> **Session:** [YYYY-MM-DD · HH:MM:SS]
> **Skill:** [Skill file name or `name` field from front matter]
> **Invocation:** [Exact command or trigger used, including any parameters supplied]
>
> **1. Session Summary**
> Total duration · Total tool calls · Total input/output tokens · Final outcome (completed / halted / partial)
>
> **2. Execution Trace**
> A table capturing every skill step and its corresponding tool call. One row per discrete action.
>
> - **Step** — Sequential number and name matching the skill's workflow (e.g. `1 · Validate input`)
> - **Instruction** — The specific directive from the skill that governed this action (one concise sentence)
> - **Tool** — Tool invoked, or `—` if no tool call was made
> - **Key Inputs** — The most meaningful arguments or parameters passed; omit noise
> - **Followed?** — `Yes`, `No`, or `Partial`
> - **Outcome** — What actually happened (e.g. `Passed`, `Halted — missing input`, `Skipped — condition not met`)
> - **Notes** — Any deviation, skip, reordering, or retry; leave blank if none
>
> **3. Errors & Warnings**
> Any failures with cause and suggested fix. If everything was healthy, state that in one sentence.

Write findings directly. Paraphrase log evidence — never paste raw JSON. Do not narrate the investigation process.
Save the report to `.github/agent-logs/agent-debug-[YYYYMMDD-HHmmss].md`.

Once the `troubleshoot` skill completes, present its findings inline in the chat so the developer can see them without opening the file.

---

## Important

- Always run `git add -A` first — never commit only staged files. The full diff is required for accurate atomicity analysis.
- Never commit if the author identity check fails. Warn clearly and stop.
- Never ask the developer to choose a commit message — generate it from the diff.
- Always resolve the story reference (Step 3) before generating any commit message.
- If the developer rejects the atomic split, always fall back to a single commit. Do not abort or ask again.
- No `Co-authored-by` trailers are used under any circumstances.