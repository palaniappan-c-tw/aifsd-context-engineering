---
name: pr-submit
description: 'Workflow for preparing and submitting a GitHub Pull Request. Use this skill when a user asks to submit a PR, open a pull request, raise a PR, or push their branch for review. Covers pre-submit checks, PR title/description generation, reviewer selection, and GitHub PR creation.'
---

# PR Submit Workflow

Step-by-step procedure for preparing a branch and submitting a well-formed GitHub Pull Request.

---

## Procedure

### Step 1: Detect the Base Branch

Do not assume the default branch is `main`. Detect it dynamically:

```bash
BASE_BRANCH=$(git remote show origin | grep 'HEAD branch' | sed 's/.*: //')
```

Use `BASE_BRANCH` in all subsequent steps.

---

### Step 2: Validate the Current Branch

1. Store the active branch name:
   ```bash
   BRANCH=$(git branch --show-current)
   ```
2. Validate the branch name matches the convention:
   ```bash
   echo "$BRANCH" | grep -qE '^(feat|fix|chore|docs|refactor|test)/.+'
   ```
   - If the command exits non-zero, the branch does **not** match. Warn the user and ask whether to proceed anyway or rename the branch first.
   - Allowed types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`
   - Examples: `feat/PROJ-123`, `fix/AUTH-42`
3. Extract the **ticket ID** from the branch name (the segment after `<type>/`).
   - Store as `TICKET_ID` for use in the PR description.

---

### Step 3: Check Commits

1. Run `git log $BASE_BRANCH..HEAD --oneline` to list commits on this branch.
2. Check whether commits are clean and meaningful (no "WIP", "fixup", "temp" messages).
3. If there are fixup or WIP commits, ask the user:
   > "There are WIP/fixup commits on this branch. Do you want to squash them before submitting? (recommended)"
   - If yes: guide the user to run `git rebase -i $BASE_BRANCH` and squash to a single meaningful commit.
   - If no: proceed.
4. Scan for ignoreable files being committed. Run `git diff $BASE_BRANCH..HEAD --name-only` and check whether any listed files match:
   - Patterns declared in `.gitignore` (if the file exists in the repo root)
   - Common build artifacts: `node_modules/`, `dist/`, `build/`, `*.log`, `.env`

   If any matches are found, warn the user:
   > "The following files appear to be build artifacts or should be ignored: `<files>`. Do you want to remove them before submitting?"
   - If yes: help the user run `git rm --cached <file>` for each offending file and amend the relevant commit.
   - If no: proceed.

---

### Step 4: Generate the PR Title

Derive the PR title using the following format:

```
[<TICKET_ID>] <Imperative summary of the change>
```

Rules:
- Use imperative mood: "Add", "Fix", "Refactor", "Remove" — not "Added" or "Adds".
- Maximum 72 characters.
- Infer the summary from the commit messages (`git log $BASE_BRANCH..HEAD --oneline`) or ask the user.

Example: `[AUTH-42] Add JWT refresh token endpoint`

---

### Step 5: Generate the PR Description

Use the template below. Derive the content from commit messages (`git log $BASE_BRANCH..HEAD --oneline`).

```markdown
## Change Summary

<!-- Bullet list of changes derived from commit messages. -->
-
-
```

---

### Step 6: Assign Reviewers

If the user provided reviewer name(s) when triggering the skill, store them as `REVIEWERS` (comma-separated GitHub usernames).

If no reviewers were provided at invocation, **skip this step entirely** — do not ask the user. Leave `REVIEWERS` unset.

---

### Step 7: Push the Branch

Before creating the PR, ensure the branch exists on the remote:

```bash
git push -u origin HEAD
```

If the push fails (e.g. rejected due to force-push protection), surface the error and stop.

---

### Step 8: Submit the PR

Resolve `OWNER` and `REPO` from the remote URL:

```bash
git remote get-url origin
```

Call the GitHub MCP `create_pull_request` tool with the following parameters:

| Parameter | Value |
|-----------|-------|
| `owner`   | Resolved from remote URL |
| `repo`    | Resolved from remote URL |
| `title`   | `GENERATED_TITLE` |
| `body`    | `GENERATED_DESCRIPTION` |
| `head`    | `BRANCH` |
| `base`    | `BASE_BRANCH` |

If reviewers were specified (`REVIEWERS`), call the MCP `update_pull_request` tool with following paramters:

| Parameter       | Value |
|-----------------|-------|
| `owner`         | Resolved from remote URL |
| `repo`          | Resolved from remote URL |
| `pull_number`   | Returned by `create_pull_request` |
| `reviewers`     | `REVIEWERS` |

Output the PR URL returned by the MCP tool.