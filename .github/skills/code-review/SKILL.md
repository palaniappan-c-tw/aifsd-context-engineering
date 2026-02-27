---
name: code-review
description: 'Structured code review workflow for checking implementation quality against project standards. Use this skill when asked to review code, a pull request, or validate implementation correctness.'
---

# Code Review Workflow

Structured procedure for reviewing code against the project's standards and best practices.

## Procedure

### Step 1: Understand the Change

- Read the PR description or the user's explanation of what changed and why.
- Identify which bounded contexts and layers are affected.
- Load the `memory` skill if domain understanding is needed.

### Step 2: Check Against Coding Standards

- Identify the file types involved (`.java`, `.tsx`, etc.).
- Review against the relevant `.github/instructions/*.instructions.md` standards.
- Flag any violations with the specific rule and a suggested fix.

### Step 3: Walk Through the Checklist

Use the [Review Checklist](review-checklist.md) systematically. For each category:
- Note any issues found.
- Classify severity: **Blocker** (must fix) / **Warning** (should fix) / **Suggestion** (consider).

### Step 4: Summarize Findings

Present findings grouped by severity:
1. **Blockers** — issues that must be resolved before merge.
2. **Warnings** — issues that should be addressed but aren't blocking.
3. **Suggestions** — optional improvements for code quality.

For each issue, provide:
- File and location.
- The violated rule or principle.
- A concrete fix or example.

## Resources

- [Review Checklist](review-checklist.md)
