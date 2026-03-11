# Conventional Commits

All commit messages must follow this format:

```
<type>(<scope>): <short description>
```

---

## Fields

| Field | Rules |
|---|---|
| `type` | See type list below |
| `scope` | Derived from the most affected module, directory, or component name |
| `short description` | Imperative mood, lowercase, no trailing period, max 72 characters |
| `body` (optional) | Include only when the change needs additional context, e.g. breaking changes |

---

## Type List

| Type | Use for |
|---|---|
| `feat` | A new feature |
| `fix` | A bug fix |
| `refactor` | Code restructuring with no behaviour change |
| `test` | Adding or updating tests |
| `chore` | Maintenance tasks, tooling, config |
| `docs` | Documentation only changes |
| `style` | Formatting, whitespace — no logic change |
| `perf` | Performance improvements |
| `ci` | CI/CD pipeline changes |

---

## Examples

```
feat(auth): add JWT middleware
fix(payments): handle null response from gateway
refactor(user): extract profile validation into helper
test(auth): add unit tests for JWT expiry
chore(deps): add jsonwebtoken package
docs(readme): update local setup instructions
ci(github): add lint step to pull request workflow
```