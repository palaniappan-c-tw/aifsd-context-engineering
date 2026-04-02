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

### Step 2: Derive the Ticket ID

Follow this priority order to obtain the ticket ID:

#### 2.1: Check if User Provided a Ticket ID

If the user provided a ticket ID when invoking this skill (e.g., "submit PR with ticket ABC-123"), use it directly:
```bash
TICKET_ID="<USER_PROVIDED_ID>"
```

#### 2.2: Extract from Branch Name

If no ticket ID was provided by the user, attempt to extract it from the branch name:

1. Store the active branch name:
   ```bash
   BRANCH=$(git branch --show-current)
   ```
2. Validate the branch name matches the convention:
   ```bash
   echo "$BRANCH" | grep -qE '^(feat|fix|chore|docs|refactor|test)/.+'
   ```
   - If the match succeeds, extract the **ticket ID** from the segment after `<type>/`:
     ```bash
     TICKET_ID=$(echo "$BRANCH" | sed -E 's/^[^/]+\///' | cut -d'/' -f1)
     ```
   - If the match fails, proceed to Step 2.3.
   - Allowed types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`
   - Examples: `feat/PROJ-123`, `fix/AUTH-42`

#### 2.3: Extract from Commit Messages

If the branch name does not contain a ticket ID, examine the commit messages:

```bash
git log $BASE_BRANCH..HEAD --pretty=format:'%B' | grep -oE '\b[A-Z][A-Z0-9]+-[0-9]+\b' | head -1
```

- If a ticket ID pattern (e.g., `PROJ-123`, `AUTH-42`) is found, store it as `TICKET_ID`.
- If no ticket ID is found, proceed to Step 2.4.

#### 2.4: Ask User How to Proceed

If no ticket ID has been derived, inform the user:

> "Could not automatically derive a ticket ID from the branch name or commit messages. How would you like to proceed?
>
> Option 1: Provide a ticket ID now (e.g., `ABC-123`)
> Option 2: Continue without a ticket ID (PR title will not include a ticket prefix)
> Option 3: Abort and rename the branch / update commits first"

- If the user provides a ticket ID, store it as `TICKET_ID`.
- If the user chooses to continue without one, set `TICKET_ID=""` (empty).
- If the user chooses to abort, stop the workflow.

---

### Step 3: Validate the Current Branch

1. Validate the branch name matches the convention:
   ```bash
   echo "$BRANCH" | grep -qE '^(feat|fix|chore|docs|refactor|test)/.+'
   ```
   - If the command exits non-zero, the branch does **not** match. Warn the user and ask whether to proceed anyway or rename the branch first.
   - Allowed types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`
   - Examples: `feat/PROJ-123`, `fix/AUTH-42`

---

### Step 4: Check Commits

1. Run `git log $BASE_BRANCH..HEAD --oneline` to list commits on this branch.
2. Check whether commits are clean and meaningful (no "WIP", "fixup", "temp" messages).
3. If there are fixup or WIP commits, ask the user:
   > "There are WIP/fixup commits on this branch. Do you want to squash them before submitting? (recommended)"
   - If yes: guide the user to run `git rebase -i $BASE_BRANCH` and squash to a single meaningful commit.
   - If no: proceed.
4. **Scan for ignorable files being committed — this step is mandatory and must not be skipped.**

   a. Collect the list of changed files:
      ```bash
      CHANGED_FILES=$(git diff $BASE_BRANCH..HEAD --name-only)
      ```

   b. Check each changed file against `.gitignore` patterns using `git check-ignore`:
      ```bash
      echo "$CHANGED_FILES" | xargs git check-ignore --no-index
      ```
      `git check-ignore --no-index` tests paths against the `.gitignore` rules regardless of whether the file is already tracked. Any file printed by this command is matched by a `.gitignore` pattern and should not be in the PR.

   c. Additionally, check for common build artifacts that may not be in `.gitignore`:
      ```bash
      echo "$CHANGED_FILES" | grep -E '(node_modules/|dist/|build/|\.log$|\.env$)'
      ```

   d. Combine the results from (b) and (c). If **any** matches are found, warn the user:
      > "The following files appear to be build artifacts or should be ignored:
      > `<files>`
      > Do you want to remove them before submitting?"
      - If yes: help the user run `git rm --cached <file>` for each offending file and amend the relevant commit.
      - If no: proceed.

---

### Step 5: Generate the PR Title

Derive the PR title using the following format:

**If `TICKET_ID` is not empty:**
```
[<TICKET_ID>] <Imperative summary of the change>
```

**If `TICKET_ID` is empty:**
```
<Imperative summary of the change>
```

Rules:
- Use imperative mood: "Add", "Fix", "Refactor", "Remove" — not "Added" or "Adds".
- Maximum 72 characters (including ticket ID if present).
- Infer the summary from the commit messages (`git log $BASE_BRANCH..HEAD --oneline`) or ask the user.

Examples:
- With ticket ID: `[AUTH-42] Add JWT refresh token endpoint`
- Without ticket ID: `Add JWT refresh token endpoint`

---

### Step 6: Generate the PR Description

Use the template below. Derive the content from commit messages (`git log $BASE_BRANCH..HEAD --oneline`).

```markdown
## Change Summary

<!-- Bullet list of changes derived from commit messages. -->
-
-
```

---

### Step 7: Assign Reviewers

If the user provided reviewer name(s) when triggering the skill, store them as `REVIEWERS` (comma-separated GitHub usernames).

If no reviewers were provided at invocation, **skip this step entirely** — do not ask the user. Leave `REVIEWERS` unset.

---

### Step 8: Push the Branch

Before creating the PR, ensure the branch exists on the remote:

```bash
git push -u origin HEAD
```

If the push fails (e.g. rejected due to force-push protection), surface the error and stop.

---

### Step 9: Submit the PR

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