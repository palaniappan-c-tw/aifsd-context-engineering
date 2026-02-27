# Project Domain Knowledge — Index

> **Read this file first.** It indexes all domain knowledge files and defines the protocol for keeping them current.

## File Lookup

| File | Contains | Read when |
|------|----------|-----------|
| [domain-model.md](domain-model.md) | Entities, fields, aggregates, lifecycle state machines | Creating/modifying entities, writing queries, defining API shapes |
| [business-rules.md](business-rules.md) | Invariants, validations, policies, terminology | Writing service logic, validation, error handling |
| [integrations.md](integrations.md) | Events, external APIs, anti-corruption rules | Working with Kafka, calling external services, crossing context boundaries |

Fully populated e-commerce examples are available in [samples/](samples/) for reference.

---

## Memory Maintenance Protocol

> **ASSUME THESE FILES MAY BE INCOMPLETE OR STALE.**
> Domain knowledge evolves as the project grows. These files are living documents.

### For AI Agents

When working on a task that uses these memory files:

1. **Read first** — Always read the relevant memory files before generating code.
2. **Detect gaps** — If you discover a domain fact during implementation that is **not documented** in these files (a new entity, an undocumented business rule, a missing event, an unnamed integration), flag it.
3. **Propose updates** — Present the update as a concrete diff/suggestion to the developer for approval. Do not silently modify memory files.
4. **Format for proposed updates**:
   ```
   📝 Memory Update Suggested — [filename]
   Section: [section name]
   Add: [what to add, in the file's existing format]
   Reason: [why this was discovered / what task revealed it]
   ```
5. **Keep it organized** — When proposing updates, merge with existing entries rather than duplicating. Remove stale entries if you can confirm they're outdated.

### For Developers

Manually update these files when:
- **New service or entity** is added to the project
- **Domain modelling session** produces new or changed entities/rules
- **Post-incident review** reveals an undocumented business rule
- **Quarterly review** — scan for stale entries, remove deprecated entities, update changed rules

### Freshness Indicators

Add a last-updated comment at the top of each file when making changes:
```markdown
<!-- Last updated: YYYY-MM-DD by [name/team] — [brief reason] -->
```
