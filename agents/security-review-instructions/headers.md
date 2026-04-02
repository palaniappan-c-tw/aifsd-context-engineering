# Transport - Security Header Audit

INPUT: proxy_snapshot.json

CHECK HEADERS (required on all responses):
- Strict-Transport-Security (HSTS)
- X-Content-Type-Options
- X-Frame-Options
- Content-Security-Policy (CSP)
- Referrer-Policy
- Permissions-Policy

CHECK HEADERS (on sensitive responses — login, auth, account, payment, API):
- Cache-Control: must include `no-store` or `no-cache, no-store`
- Pragma: `no-cache` (for HTTP/1.0 compatibility)

SEVERITY GUIDANCE:
- **High**: Missing CSP, missing HSTS on auth/payment endpoints
- **Medium**: Missing X-Content-Type-Options, missing X-Frame-Options, missing Cache-Control on sensitive responses
- **Low**: Missing Referrer-Policy, missing Permissions-Policy

TASK:

1. For each response in proxy_snapshot.json:
   - Check presence of all required headers listed above
   - For sensitive endpoints, additionally check Cache-Control and Pragma
   - Record each missing header with its severity

2. For each finding, include:
   - Request URL
   - Response status code
   - Missing header(s)
   - Severity (High / Medium / Low)
   - Risk description

3. Deduplicate: if the same host consistently misses the same header, group findings by host rather than listing every URL.

OUTPUT: findings_headers.md

OUTPUT SCHEMA:

```markdown
# Security Header Findings

## Summary
- Total responses analyzed: <N>
- Responses with missing headers: <N>
- High: <N> | Medium: <N> | Low: <N>

## Findings

### [Severity] — Missing <Header> on <host>
- **URLs affected**: <URL list or count>
- **Status codes**: <observed codes>
- **Risk**: <description>
```