# Transport - CORS Misconfiguration

INPUT: proxy_snapshot.json

CHECK HEADERS:
- Access-Control-Allow-Origin
- Access-Control-Allow-Credentials
- Access-Control-Allow-Headers
- Access-Control-Allow-Methods

FLAG CONDITIONS:

1. **Wildcard origin**: `Access-Control-Allow-Origin: *`
   - Severity: Medium (High if credentials also allowed)

2. **Reflected origin**: Compare the request `Origin` header value against the response `Access-Control-Allow-Origin` value. If they match exactly, the server is reflecting the origin without validation.
   - Severity: High

3. **Credentials with permissive origin**: `Access-Control-Allow-Credentials: true` combined with wildcard or reflected origin.
   - Severity: High

4. **Overly permissive methods**: `Access-Control-Allow-Methods` includes dangerous methods (`PUT`, `DELETE`, `PATCH`) without justification.
   - Severity: Medium

TASK:

1. For each response in proxy_snapshot.json that contains CORS headers:
   - Check all flag conditions above
   - Compare request Origin (if present) against response Allow-Origin
   - Assess combined risk of credentials + origin policy

2. For each finding, include:
   - Request URL
   - Request Origin header (if present)
   - Response CORS header name(s) and value(s)
   - Response status code
   - Severity (High / Medium / Low)
   - Risk description

OUTPUT: findings_cors.md

OUTPUT SCHEMA:

```markdown
# CORS Findings

## Summary
- Responses with CORS headers: <N>
- Misconfigured: <N>
- High: <N> | Medium: <N> | Low: <N>

## Findings

### [Severity] — <issue type> on <URL>
- **Request Origin**: <value or N/A>
- **Response headers**: <header: value pairs>
- **Status code**: <code>
- **Risk**: <description>
```