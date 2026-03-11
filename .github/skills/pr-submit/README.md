# PR-SUBMIT Skill

Raise a well-formed GitHub Pull Request without leaving editor/IDE — no GitHub UI, no `gh` CLI required.

---

## Value

| Pain Point | What This Skill Does |
|---|---|
| Context switching to GitHub UI or terminal | Submits the PR entirely from within Copilot Chat using a single command |
| Writing PR descriptions from scratch | Auto-generates a concise **Change Summary** from your individual commit messages, giving reviewers an instant overview |
| Non-standard branch names slipping through | Validates the branch name against the project convention (`feat/`, `fix/`, `chore/`, etc.) and warns you before the PR is created |
| Accidentally committing build artifacts or ignored files | Scans the diff for files matching `.gitignore` patterns and common build artifacts, prompting you to remove them before raising the PR |

---

## Prerequisites

| Requirement | Details |
|---|---|
| **GitHub MCP server** | Must be configured and authenticated in VS Code. The skill uses the `create_pull_request` and `request_reviewers` MCP tools to interact with GitHub. |
| **Git** | `git` must be installed and the repository must have a remote named `origin` pointing to GitHub. |
| **Authenticated remote** | Your local Git must have push access to `origin` (SSH key or HTTPS credential configured). |
| **At least one commit ahead of base** | The branch must have commits not yet on the base branch — otherwise there is nothing to PR. |

---

## How to Use

Trigger the skill in Copilot Chat:

```
/submit pr
```

```
raise a PR
```

```
open a pull request
```

Optionally, specify reviewers at invocation:

```
raise a PR, reviewer: @alice @bob
```

---

## What Happens Under the Hood

1. **Detects the base branch** dynamically — never assumes `main`.
2. **Validates branch naming** against `feat|fix|chore|docs|refactor|test/<id>` convention and warns on mismatch.
3. **Scans commits** for WIP / fixup messages and offers to squash them.
4. **Checks for ignorable files** (build artifacts, `.env`, `node_modules/`, etc.) and prompts to remove before proceeding.
5. **Generates a PR title** in `[TICKET-ID] Imperative summary` format from commit messages.
6. **Generates a PR description** as a bullet-list Change Summary derived from individual commits — useful context for the reviewer without manual effort.
7. **Pushes the branch** to origin if not already pushed.
8. **Creates the PR** via the GitHub MCP tool and returns the PR URL.
9. **Assigns reviewers** if specified at invocation.

---

## Pre-submit Checks Summary

| Check | Behaviour on Failure |
|---|---|
| Branch name convention | Warn and ask to proceed or rename |
| WIP / fixup commits | Offer to squash via interactive rebase |
| Ignored / artifact files in diff | List offending files and offer to remove with `git rm --cached` |

---

## Example Output

```
PR created: https://github.com/org/repo/pull/42

Title : [AUTH-42] Add JWT refresh token endpoint

Change Summary:
- Add refresh token generation on login
- Validate token expiry on protected routes
- Add unit tests for token service
```
