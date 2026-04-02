# Transport - HTTP vs HTTPS Enforcement

INPUT: proxy_snapshot.json

TASK:

1. **Identify sensitive endpoints** by matching URL paths containing:
   - `login`, `signin`, `auth`, `oauth`, `token`
   - `account`, `profile`, `user`, `password`, `reset`
   - `payment`, `checkout`, `billing`, `invoice`, `order`

2. **Check for HTTP traffic**:
   - Scan proxy_snapshot.json for any requests using `http://` (not `https://`)
   - For HTTP requests, check if the response is a 301/302 redirect to an HTTPS URL
   - Flag any HTTP request that serves content (2xx) without redirecting to HTTPS

3. **Evaluate HSTS on HTTPS responses**:
   - Check for `Strict-Transport-Security` header presence
   - Flag **missing HSTS** entirely
   - Flag **weak HSTS** configurations:
     - `max-age` < 31536000 (1 year)
     - Missing `includeSubDomains` directive
     - Missing `preload` directive (informational)

4. **Highlight endpoints vulnerable to**:
   - SSL stripping attacks (HTTP served without redirect)
   - Downgrade attacks (weak or missing HSTS)

SEVERITY GUIDANCE:
- **High**: Sensitive endpoint served over HTTP without redirect; missing HSTS on auth/payment endpoints
- **Medium**: Weak HSTS (low max-age, no includeSubDomains); non-sensitive endpoint over HTTP
- **Low**: Missing `preload` directive

For each finding, include:
- Request URL
- Protocol (HTTP / HTTPS)
- Response status code
- HSTS header value (if present) or "MISSING"
- Severity (High / Medium / Low)
- Risk description

OUTPUT: findings_https.md

OUTPUT SCHEMA:

```markdown
# HTTPS Enforcement Findings

## Summary
- Total sensitive endpoints: <N>
- HTTP without redirect: <N>
- Missing/weak HSTS: <N>
- High: <N> | Medium: <N> | Low: <N>

## Findings

### [Severity] — <issue type> on <URL>
- **Protocol**: HTTP | HTTPS
- **Status code**: <code>
- **HSTS**: <value or MISSING>
- **Risk**: <description>
```