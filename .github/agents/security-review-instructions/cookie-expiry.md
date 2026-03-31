# Session - Cookie Expiry & Scope

INPUT: proxy_snapshot.json

CHECK ATTRIBUTES (on all Set-Cookie headers):
- `Expires` — absolute expiration date
- `Max-Age` — relative expiration in seconds
- `Path` — URL path scope
- `Domain` — domain scope

SESSION COOKIE HEURISTICS:
Identify cookies likely carrying session state by name patterns:
- `session`, `sess`, `sid`, `JSESSIONID`, `PHPSESSID`, `ASP.NET_SessionId`
- `auth`, `token`, `jwt`, `access_token`, `refresh_token`
- Any cookie set during login/auth responses

SEVERITY GUIDANCE:
- **High**: Session cookie with `Max-Age` > 86400 (24 hours) or far-future `Expires` (> 24 hours from response date)
- **Medium**: Any cookie scoped to root path (`/`) with broad domain (e.g., `.deere.com` rather than specific subdomain); session cookie without explicit expiry (relies on browser session — acceptable but worth noting)
- **Low**: Non-session cookie with excessive lifetime (> 1 year)

TASK:

1. Extract all cookies from `Set-Cookie` response headers in proxy_snapshot.json.

2. For each cookie:
   - Parse `Expires`, `Max-Age`, `Path`, and `Domain` attributes
   - Classify as session or non-session using heuristics above
   - Flag excessively long lifetimes based on severity thresholds
   - Flag overly broad scope (`Path=/` combined with parent domain)

3. For each finding, include:
   - Request URL that set the cookie
   - Cookie name
   - Classification (session / non-session)
   - `Expires` or `Max-Age` value
   - `Path` and `Domain` values
   - Severity (High / Medium / Low)
   - Risk description

4. Deduplicate: if the same cookie name is set across multiple URLs on the same host with the same expiry/scope issues, group by host and cookie name.

OUTPUT: findings_cookie_expiry.md

OUTPUT SCHEMA:

```markdown
# Cookie Expiry & Scope Findings

## Summary
- Total cookies analyzed: <N>
- Cookies with expiry/scope issues: <N>
- High: <N> | Medium: <N> | Low: <N>

## Findings

### [Severity] — <issue type> on `<cookie_name>` (<host>)
- **Classification**: session | non-session
- **Set by**: <URL list or count>
- **Expires / Max-Age**: <value>
- **Path**: <value>
- **Domain**: <value>
- **Risk**: <description>
```