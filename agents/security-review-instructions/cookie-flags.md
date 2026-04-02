# Session - Cookie Flag Analysis

INPUT: proxy_snapshot.json

CHECK FLAGS (on all Set-Cookie headers and response cookies):
- `HttpOnly` — prevents JavaScript access to the cookie
- `Secure` — ensures cookie is only sent over HTTPS
- `SameSite` — controls cross-site request behavior (`Strict`, `Lax`, or `None`)

SESSION COOKIE HEURISTICS:
Identify cookies likely carrying session state by name patterns:
- `session`, `sess`, `sid`, `JSESSIONID`, `PHPSESSID`, `ASP.NET_SessionId`
- `auth`, `token`, `jwt`, `access_token`, `refresh_token`
- Any cookie set during login/auth responses

SEVERITY GUIDANCE:
- **High**: Session cookie missing `HttpOnly` or `Secure`
- **Medium**: Session cookie missing `SameSite` or set to `SameSite=None` without `Secure`
- **Low**: Non-session cookie missing flags

TASK:

1. Extract all cookies from `Set-Cookie` response headers in proxy_snapshot.json.

2. For each cookie:
   - Parse name, value, and flag attributes
   - Classify as session or non-session using heuristics above
   - Check presence of `HttpOnly`, `Secure`, and `SameSite` flags
   - Assign severity based on classification and missing flags

3. For each finding, include:
   - Request URL that set the cookie
   - Cookie name
   - Classification (session / non-session)
   - Missing flag(s)
   - Severity (High / Medium / Low)
   - Risk description

4. Deduplicate: if the same cookie name is set across multiple URLs on the same host with the same missing flags, group by host and cookie name.

OUTPUT: findings_cookie_flags.md

OUTPUT SCHEMA:

```markdown
# Cookie Flag Findings

## Summary
- Total cookies analyzed: <N>
- Cookies with missing flags: <N>
- High: <N> | Medium: <N> | Low: <N>

## Findings

### [Severity] — Missing <flag> on `<cookie_name>` (<host>)
- **Classification**: session | non-session
- **Set by**: <URL list or count>
- **Missing flags**: <list>
- **Risk**: <description>
```