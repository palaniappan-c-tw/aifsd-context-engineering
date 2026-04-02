# SQLi via JSON Body — Injection Analysis

INPUT: proxy_snapshot.json

TASK:

1. **Identify candidates** — scan all requests for POST/PUT/PATCH methods that contain a JSON body (`Content-Type: application/json`).

2. **Extract key-value pairs** — parse every JSON body and extract all key-value pairs (including nested keys using dot notation, e.g., `filter.field`).

3. **Flag database-interaction keys** — mark any key whose name matches or contains these patterns (case-insensitive):
   - `id`, `user_id`, `account_id`, `order_id`, `record_id`
   - `query`, `filter`, `search`, `where`, `sort`, `order_by`, `group_by`
   - `column`, `table`, `field`, `select`, `limit`, `offset`
   - `name` (only when value looks like it could be interpolated into SQL — e.g., contains quotes or special chars)

4. **Replay original request**:
   - Use `send_http1_request` or `send_http2_request` (match the protocol version of the original)
   - Confirm it returns a successful response (2xx)
   - If replay fails, skip this endpoint and note the failure

5. **Inject SQL payloads** into each flagged parameter, one at a time:
   - **String-based**: `' OR '1'='1`, `' OR ''='`, `'; --`, `' UNION SELECT NULL--`
   - **Numeric-based**: `1 OR 1=1`, `1; DROP TABLE test--`, `0 UNION SELECT NULL--`
   - **Time-based blind**: `' OR SLEEP(5)--`, `1; WAITFOR DELAY '0:0:5'--`, `' OR pg_sleep(5)--`
   - **Error-based**: `'`, `''`, `\`, `1'`
   - Keep all other parameters unchanged when testing one parameter

6. **Analyze responses**:
   - **SQL error in response body** (e.g., `SQL syntax`, `ORA-`, `PG::`, `mysql_`, `sqlite3`, `JDBC`, `unterminated`) → **High — Error-based SQLi**
   - **Response time ≥ 5 seconds on time-based payload** → **High — Blind SQLi**
   - **Different row count or data structure returned** vs. original → **Medium — Possible SQLi**
   - **200 with identical response** → **Low — Payload accepted but no observable effect**
   - **400/422 with input validation error** → **Safe — Input validated**
   - **403/401** → **Safe — Rejected before processing**

AUTHORIZATION CONTEXT:
- Preserve the original request's auth headers (Bearer token, session cookie, API key) when replaying
- Record the auth mechanism used for each test

RATE LIMITING:
- Insert a 500ms delay between consecutive injection requests to avoid WAF triggers
- If a 429 (Too Many Requests) response is received, back off for 5 seconds before continuing
- Limit to a maximum of 10 injection payloads per parameter per endpoint
- If requests start returning 403 consistently, stop testing that endpoint (possible WAF block)

For each finding, include:
- Endpoint URL
- HTTP method
- Parameter name (JSON key path)
- Original value
- Injected payload
- Response status code
- Response time (if time-based test)
- Error string found (if error-based)
- Severity (High / Medium / Low / Safe)
- Auth mechanism used

OUTPUT: findings_sqli_json.md

OUTPUT SCHEMA:

```markdown
# SQLi via JSON Body Findings

## Summary
- Endpoints scanned: <N>
- JSON bodies with DB-interaction keys: <N>
- Potential SQLi vulnerabilities: <N>
- High: <N> | Medium: <N> | Low: <N>

## Flagged Parameters
| Endpoint | Method | Key | Original Value | Reason Flagged |
|----------|--------|-----|----------------|----------------|
| ...      | ...    | ... | ...            | ...            |

## Findings

### [Severity] — SQLi on <endpoint>
- **Method**: POST | PUT | PATCH
- **Parameter**: <JSON key path> = <original> → <injected payload>
- **Original status**: <code>
- **Injected status**: <code>
- **Response time**: <ms> (if time-based)
- **Error string**: <string> (if error-based)
- **Auth mechanism**: <type>
- **Evidence**: <brief response diff or error excerpt>

## Skipped Endpoints
- <endpoint> — <reason>
```
