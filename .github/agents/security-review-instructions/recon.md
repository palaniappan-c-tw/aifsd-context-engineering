# Recon - Host & Subdomain Enumeration

INPUT: proxy_snapshot.json

TASK:

1. Extract all unique hosts, subdomains, and ports from request URLs and Host headers.

2. Classify each host:
   - **Internal/non-production** — match patterns like:
     - `dev.*`, `staging.*`, `stg.*`, `uat.*`, `qa.*`, `test.*`
     - `*.internal.*`, `*.corp.*`, `*.local`
     - Any host resolving to RFC 1918 ranges (10.x, 172.16-31.x, 192.168.x)
   - **Admin interfaces** — match patterns like:
     - `admin.*`, `manage.*`, `console.*`, `dashboard.*`, `portal.*`, `cms.*`
     - URL paths containing `/admin`, `/management`, `/actuator`, `/console`
   - **Non-standard ports** — flag ports other than 80/443 (e.g., 8080, 8443, 9090, 3000)

3. For each finding, include:
   - Host / subdomain
   - Port
   - Classification (internal, admin, non-standard port)
   - Number of requests observed
   - Severity: High (admin/internal exposed), Medium (non-standard port), Low (informational)

OUTPUT: findings_recon.md

OUTPUT SCHEMA:

```markdown
# Recon Findings

## Summary
- Total unique hosts: <N>
- Flagged hosts: <N>

## Findings

### [Severity] — <host:port>
- **Classification**: internal | admin | non-standard-port
- **Requests observed**: <N>
- **Evidence**: <example URL>
- **Risk**: <brief description>
```