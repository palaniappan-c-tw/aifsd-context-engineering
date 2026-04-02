# Agents

## **SECURITY-REVIEWER** 

End-to-end security audit orchestrator using Burp Suite MCP tools with parallel analysis of proxy traffic.
  ### *security-review-instrutions*
  - **recon.md** — Host and subdomain enumeration task that classifies discovered hosts as internal, admin, or non-standard port.
  - **cookie-expiry.md** — Analyzes Set-Cookie headers for excessive lifetimes and overly broad scope.
  - **cookie-flags.md** — Analyzes Set-Cookie headers for missing security flags (HttpOnly, Secure, SameSite).
  - **cors.md** — Detects CORS misconfigurations including wildcard origins, reflected origins, and over-permissive methods.
  - **fingerprint.md** — Identifies technology stack disclosure through response headers that reveal framework, server, or version information.
  - **headers.md** — Audits security headers (HSTS, CSP, X-Frame-Options, Cache-Control, etc.) on HTTP responses.
  - **https.md** — Checks HTTP vs HTTPS enforcement on sensitive endpoints and validates HSTS configuration.
  - **idor.md** — Tests for Insecure Direct Object Reference vulnerabilities by mutating identifier parameters and comparing responses.
  - **numeric-boundary.md** — Tests numeric boundary abuse in business logic parameters using edge cases and overflow values.
  - **sqli-json.md** — Tests for SQL injection vulnerabilities in JSON request bodies using string, time-based blind, and error-based payloads.

[SETUP GUIDE](/agents/SECURITY-REVIEW.md)

## **PR-REVIEWER**
Comprehensive PR review orchestrator that spawns parallel subagents for code quality, test coverage, error handling, and type design analysis.

[READ ME](/agents/PR-REVIEW.md)
