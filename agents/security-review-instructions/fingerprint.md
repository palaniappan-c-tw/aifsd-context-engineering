# Transport - Tech Stack Fingerprinting

INPUT: proxy_snapshot.json

CHECK HEADERS:
- Server (e.g., `Apache/2.4.51`, `nginx/1.21.0`)
- X-Powered-By (e.g., `Express`, `ASP.NET`, `PHP/8.1`)
- X-AspNet-Version
- X-AspNetMvc-Version
- X-Generator
- X-Drupal-Cache, X-Drupal-Dynamic-Cache
- X-Request-Id, X-Correlation-Id (internal architecture leakage)
- X-Debug, X-Debug-Token, X-Debug-Token-Link
- Any other verbose or non-standard headers revealing framework, server, or language details

SEVERITY GUIDANCE:
- **High**: Version-specific disclosure (e.g., `Server: Apache/2.4.49` — known CVE), debug headers in production
- **Medium**: Technology disclosure without version (e.g., `X-Powered-By: Express`)
- **Low**: Generic server identification (e.g., `Server: nginx`)

TASK:

1. Inspect all response headers in proxy_snapshot.json
2. Identify any header+value pair that discloses technology stack information
3. Cross-reference version strings against known CVE patterns where possible
4. For each finding, include:
   - Request URL
   - Header name and value
   - Response status code
   - Severity (High / Medium / Low)
   - Risk: how the disclosed information could aid fingerprinting or targeted attacks

5. Deduplicate: group by host if the same header appears consistently.

OUTPUT: findings_fingerprint.md

OUTPUT SCHEMA:

```markdown
# Fingerprint Findings

## Summary
- Total info-leaking responses: <N>
- High: <N> | Medium: <N> | Low: <N>

## Findings

### [Severity] — <Header>: <value> on <host>
- **URLs affected**: <URL list or count>
- **Status code**: <code>
- **Risk**: <description>
```