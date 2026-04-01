# AIFSD-CONTEXT-ENGINEERING

Context engineering setup for AI-assisted development — custom agents, skills, and memory files for GitHub Copilot.

## `agents/`
- **security-reviewer.md** — End-to-end security audit orchestrator using Burp Suite MCP tools with parallel analysis of proxy traffic.
    - **security-review-instrutions**
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

    - **SECURITY-REVIEW.md** — Setup guide for Burp Suite Community Edition with MCP Server extension, capturing proxy traffic, and running the security-reviewer agent. []

- **pr-reviewer.md** — Comprehensive PR review orchestrator that spawns parallel subagents for code quality, test coverage, error handling, and type design analysis.
    - **pr-code-reviewer.md** — Reviews code quality, style, naming, layering, security, and compliance with project instruction files.

    - **pr-silent-failure-hunter.md** — Hunts for silent failures, swallowed exceptions, missing error logging, and inadequate error handling in changed code.

    - **pr-test-analyzer.md** — Reviews test coverage quality and completeness including behavioural coverage, edge cases, and assertion quality.

    - **pr-type-design-analyzer.md** — Analyzes design quality of types, models, entities, DTOs, and value objects including encapsulation and invariant expression.

## `memory/` *(placeholder for now)*

## `skills/`
- **commit**: Six-step workflow for creating atomic commits with author validation and generated messages. [README](/.github/skills/commit/README.md)
- **pr-submit**: Step-by-step workflow for preparing and submitting a GitHub PR with branch validation and commit analysis. [README](/.github/skills/pr-submit/README.md)
