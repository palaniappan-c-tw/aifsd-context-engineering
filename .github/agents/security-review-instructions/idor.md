# IDOR - Parameter Manipulation

INPUT: proxy_snapshot.json

TASK:

1. **Identify IDOR candidates** — scan all requests for user-supplied identifier parameters:
   - Query params and path segments: `id`, `user_id`, `account_id`, `uid`, `uuid`, `profile_id`, `order_id`, `invoice_id`, `ticket_id`, `doc_id`, `file_id`, `resource_id`
   - Also check POST/PUT body parameters and JSON fields with the same names

2. **Replay original request**:
   - Use `send_http1_request` or `send_http2_request` to replay the original request (match the protocol version of the original)
   - Confirm it returns 200 with user-specific data
   - If replay fails, skip this endpoint and note the failure

3. **Mutate identifier parameters**:
   - Increment: e.g., `id=123` → `id=124`, `id=125`
   - Decrement: e.g., `id=123` → `id=122`, `id=121`
   - Boundary values: `id=1`, `id=2`, `id=0`, `id=-1`
   - Substitute with another known user's ID if one is visible in other requests

4. **Send modified requests** via `send_http1_request` or `send_http2_request` Burpsuite MCP tools (match the protocol version of the original)

5. **Compare responses**:
   - Different data returned → **Potential IDOR vulnerability** (High)
   - 403/401 → Access control in place (Safe)
   - 404 → Record not found (note if inconsistent with other IDs — may indicate enumeration)
   - Same data always → Session-bound (Safe)

AUTHORIZATION CONTEXT:
- Note which auth mechanism each request uses (Bearer token, session cookie, API key)
- If multiple user sessions are visible in proxy history, test cross-user access by replaying a request with User A's identifier but User B's auth token (horizontal privilege escalation)
- Record the auth context used for each test in findings

RATE LIMITING:
- Insert a 500ms delay between consecutive mutated requests to avoid WAF triggers
- If a 429 (Too Many Requests) response is received, back off for 5 seconds before continuing
- Limit to a maximum of 10 mutation attempts per endpoint
- If requests start returning 403 consistently, stop testing that endpoint (possible WAF block)

For each finding, include:
- Endpoint URL
- HTTP method
- Parameter name
- Original ID value
- Tested ID value
- Response status code
- Whether different user data was exposed (yes/no)
- Auth mechanism used
- Severity (High / Medium / Low / Safe)

OUTPUT: findings_idor.md

OUTPUT SCHEMA:

```markdown
# IDOR Findings

## Summary
- Endpoints tested: <N>
- Potential IDOR vulnerabilities: <N>
- High: <N> | Medium: <N> | Low: <N>

## Findings

### [Severity] — IDOR on <endpoint>
- **Method**: GET | POST | PUT | DELETE
- **Parameter**: <name> = <original> → <tested>
- **Original status**: <code>
- **Mutated status**: <code>
- **Data exposed**: yes | no
- **Auth mechanism**: <type>
- **Evidence**: <brief response diff summary>

## Skipped Endpoints
- <endpoint> — <reason>
```