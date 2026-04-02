# Atomicity Guide

A commit is **atomic** when all of its changes serve a single, clearly stateable purpose — one that could be described naturally in a single commit message sentence.

---

## Treat changes as NON-ATOMIC if:

- Changes span multiple unrelated concerns (e.g. a bug fix alongside a new feature in the same diff)
- Changes touch both production code and test code in logically separable ways
- Changes include dependency updates alongside feature code
- Changes affect multiple unrelated modules with no shared purpose

## Treat changes as ATOMIC if:

- All changes serve a single, clearly stateable purpose
- The changes would be described naturally with one commit message sentence

---

## Granularity

When splitting a non-atomic diff, choose the granularity that best represents the actual logical separation — file-level, hunk-level, or logical grouping. There is no fixed rule; use judgement based on the nature of the changes.