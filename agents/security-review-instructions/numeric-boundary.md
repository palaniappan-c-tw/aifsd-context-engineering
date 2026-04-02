# Numeric Boundary Abuse — Business Logic Analysis

INPUT: proxy_snapshot.json

TASK:

1. **Identify candidates** — scan all requests (any method) for numeric fields in:
   - JSON body parameters (POST/PUT/PATCH with `Content-Type: application/json`)
   - Query string parameters
   - Form-encoded body parameters
   - Focus on fields whose names suggest financial or quantity values (case-insensitive):
     - `amount`, `price`, `cost`, `total`, `subtotal`, `fee`, `charge`, `balance`, `payment`
     - `quantity`, `qty`, `count`, `units`, `num`, `number`
     - `discount`, `tax`, `rate`, `percentage`, `percent`
     - `credit`, `debit`, `refund`, `tip`, `shipping`
     - Any other field containing a numeric value (integer or decimal)

2. **Replay original request**:
   - Use `send_http1_request` or `send_http2_request` (match the protocol version of the original)
   - Confirm it returns a successful response (2xx)
   - Record the original response body for comparison
   - If replay fails, skip this endpoint and note the failure

3. **Mutate numeric values** — for each identified numeric field, replay the request with these test values (one at a time, keeping other fields unchanged):
   - **Negative**: `-1`, `-100`, `-99999`
   - **Zero**: `0`
   - **Decimal/fraction**: `0.01`, `0.001`, `0.0001`, `99.999`
   - **Very large**: `999999999`, `2147483647` (INT_MAX), `9999999999999999`
   - **Overflow**: `2147483648` (INT_MAX + 1), `-2147483649` (INT_MIN - 1), `99999999999999999999`
   - **String-as-number**: `"NaN"`, `"Infinity"`, `"-Infinity"` (only for JSON bodies)

4. **Analyze responses**:
   - **200 with negative total / reversed charge** → **High — Negative value accepted (charge reversal)**
   - **200 with zero-cost transaction completing** → **High — Zero value bypass (free goods)**
   - **200 with integer overflow / wrap-around** (e.g., large negative becomes positive or vice versa) → **High — Integer overflow**
   - **200 with unexpected calculation result** (total doesn't match expected math) → **Medium — Numeric logic error**
   - **200 with fractional value accepted where only integer expected** → **Medium — Precision abuse**
   - **500 / server error on boundary value** → **Medium — Unhandled edge case**
   - **400/422 with validation error** → **Safe — Server validates range**
   - **200 with identical response / no effect** → **Low — Value accepted but no observable impact**

AUTHORIZATION CONTEXT:
- Preserve the original request's auth headers (Bearer token, session cookie, API key) when replaying
- Record the auth mechanism used for each test

RATE LIMITING:
- Insert a 500ms delay between consecutive mutated requests to avoid WAF triggers
- If a 429 (Too Many Requests) response is received, back off for 5 seconds before continuing
- Limit to a maximum of 12 mutation values per parameter per endpoint
- If requests start returning 403 consistently, stop testing that endpoint (possible WAF block)

For each finding, include:
- Endpoint URL
- HTTP method
- Parameter name (and location: body/query/path)
- Original value
- Test value sent
- Response status code
- Observed behavior (e.g., negative total, zero-cost order, overflow)
- Severity (High / Medium / Low / Safe)
- Auth mechanism used

OUTPUT: findings_numeric_boundary.md

OUTPUT SCHEMA:

```markdown
# Numeric Boundary Abuse Findings

## Summary
- Endpoints scanned: <N>
- Numeric fields tested: <N>
- Potential boundary abuse vulnerabilities: <N>
- High: <N> | Medium: <N> | Low: <N>

## Flagged Parameters
| Endpoint | Method | Parameter | Location | Original Value | Type |
|----------|--------|-----------|----------|----------------|------|
| ...      | ...    | ...       | body/query | ...          | financial/quantity/other |

## Findings

### [Severity] — Numeric Boundary on <endpoint>
- **Method**: GET | POST | PUT | PATCH | DELETE
- **Parameter**: <name> (<location>) = <original> → <test value>
- **Original status**: <code>
- **Test status**: <code>
- **Observed behavior**: <description of what happened>
- **Auth mechanism**: <type>
- **Evidence**: <brief response excerpt showing the issue>

## Skipped Endpoints
- <endpoint> — <reason>
```
