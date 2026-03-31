# Burp Suite Community Edition & Security Review Agent — Setup Guide

End-to-end guide for setting up Burp Suite Community Edition with the MCP Server extension, capturing proxy traffic, and running the `security-reviewer` agent in VS Code to perform automated security analysis.

---

## Workflow Overview

| 1. SETUP | ➡️ | 2. CAPTURE | ➡️ | 3. ANALYSE |
|:---|:---:|:---|:---:|:---|
| Install Burp Suite | | Open Burp's browser | | Switch to `@security-reviewer` |
| Build MCP extension | | Navigate target app | | Provide target scope |
| Configure VS Code MCP connection | | Perform user workflows | | Agent fetches proxy history |
| | | Traffic → Proxy History | | Runs 10 parallel checks |
| | | | | Generates audit report |

---

## 1. Prerequisites

Before starting, ensure the following are installed and available:

| Requirement | Verification Command | Notes |
|---|---|---|
| **Java** | `java --version` | Must be on your system `PATH` |
| **`jar` command** | `jar --version` | Required for building the MCP extension |
| **Burp Suite Community Edition** | — | [Download here](https://portswigger.net/burp/communitydownload) |
| **VS Code** | `code --version` | With GitHub Copilot extension installed |

---

## 2. Building the MCP Server Extension

The MCP Server Extension allows Burp Suite to expose its proxy history and request-sending capabilities to VS Code via the Model Context Protocol.

### 2.1 Clone the Repository

```shell
git clone https://github.com/PortSwigger/mcp-server.git
```

### 2.2 Build the Extension JAR

```shell
cd mcp-server
./gradlew embedProxyJar
```

This produces two artefacts:

| Artefact | Path | Purpose |
|---|---|---|
| **Extension JAR** | `build/libs/burp-mcp-all.jar` | Loaded into Burp Suite as an extension |
| **Proxy JAR** | `libs/mcp-proxy-all.jar` | Used by VS Code's MCP client to connect to Burp |

> **Important**: These are two different JARs. The extension JAR goes into Burp Suite; the proxy JAR goes into your VS Code MCP configuration.

---

## 3. Loading the Extension into Burp Suite

1. **Open Burp Suite Community Edition**
2. Navigate to the **Extensions** tab
3. Click **Add**
4. Set **Extension Type** to `Java`
5. Click **Select file ...** and choose `build/libs/burp-mcp-all.jar` (built in Step 2.2)
6. Click **Next** to load the extension

Once loaded successfully, the MCP Server Extension will be active. It starts an SSE server on `http://127.0.0.1:9876` that VS Code will connect to.

---

## 4. Configuring VS Code MCP Connection

Create or update the file `.vscode/mcp.json` in your project workspace with the following configuration:

```jsonc
{
  "servers": {
    "burpsuite-mcp": {
      "command": "<path-to-java-executable>",
      "args": [
        "-jar",
        "<path-to-mcp-server>/libs/mcp-proxy-all.jar",
        "--sse-url",
        "http://127.0.0.1:9876"
      ]
    }
  }
}
```

### macOS Example

```jsonc
{
  "servers": {
    "burpsuite-mcp": {
      "command": "/opt/homebrew/opt/openjdk@21/bin/java",
      "args": [
        "-jar",
        "/Users/username/Documents/mcp-server/libs/mcp-proxy-all.jar",
        "--sse-url",
        "http://127.0.0.1:9876"
      ]
    }
  }
}
```

> Replace `/Users/username/Documents/mcp-server` with the actual path where you cloned the repository.

### Configuration Parameters

| Parameter | Value | Description |
|---|---|---|
| `command` | Path to `java` executable | The Java binary used to run the MCP proxy |
| `-jar` | Path to `mcp-proxy-all.jar` | The proxy JAR from the cloned repo's `libs/` directory |
| `--sse-url` | `http://127.0.0.1:9876` | SSE endpoint exposed by the Burp Suite MCP extension |

### Verification

After saving the configuration:

1. Ensure Burp Suite is running with the MCP extension loaded
2. Restart VS Code (or reload the window)
3. The MCP server should connect automatically — you can verify by checking that `burpsuite-mcp` tools are available in Copilot Chat

---

## 5. Capturing Proxy Traffic

With Burp Suite running and the MCP extension active, you now need to capture HTTP traffic from your target application.

### 5.1 Open Burp's Built-in Browser

1. In Burp Suite, go to the **Proxy** tab
2. Click **Open browser**
3. A Chromium browser opens — **all traffic from this browser is automatically proxied through Burp Suite**

> **Important**: You must use Burp's built-in browser (not your system browser) for traffic to appear in the proxy history.

### 5.2 Navigate to the Target Application

Open your target application in the Burp browser. For this example, we use `shop-cert.deere.com`.

### 5.3 Perform Representative User Workflows

Execute a variety of user actions to generate comprehensive HTTP traffic coverage. The more diverse the actions, the more thorough the security analysis will be.

**Example actions for shop-cert.deere.com:**

| Action | What It Captures |
|---|---|
| Browse product catalog | GET requests, search queries, pagination |
| Add products to cart | POST/PUT requests with product IDs, quantities |
| View cart | Cart API endpoints, session/cookie handling |
| Remove products from cart | DELETE requests, state mutation endpoints |
| Add products to save-for-later | Wishlist/save-later API calls |
| Remove products from save-for-later | Additional DELETE endpoints |
| Move products from save-for-later to cart | Cross-resource operations |
| Apply discount codes | Input validation endpoints |
| Update quantities | Parameter manipulation endpoints |

### 5.4 Verify Proxy History

After performing the actions:

1. Go to **Proxy → HTTP history** in Burp Suite
2. Confirm that requests to your target domain are listed
3. You should see a mix of GET, POST, PUT, DELETE requests across various API endpoints

---

## 6. Running the Security Review Agent

With proxy traffic captured, switch to VS Code to run the automated security analysis.

### 6.1 Invoke the Agent

In VS Code, open **Copilot Chat** and switch to the `security-reviewer` agent. You can do this by:

- Selecting `security-reviewer` from the agent/mode picker dropdown in Chat, **or**
- Typing `/agents` in the Chat input and selecting security-reviewer

### 6.2 Provide the Target Scope

Enter a prompt specifying the scope of the review:

```
Run a security review for scope shop-cert.deere.com
```

If you don't specify a scope, the agent defaults to `*.deere.com`.

### 6.3 What the Agent Does

The `security-reviewer` agent orchestrates a multi-step security audit automatically:

| Step | Action | Detail |
|---|---|---|
| **Step 0** | Initialisation | Creates an output folder `security_review_<timestamp>/` and working files |
| **Step 1** | Fetch Proxy History | Connects to Burp Suite via MCP (`get_proxy_http_history_regex`) and fetches proxy traffic in chunks using host-based regex filters |
| **Step 2** | Normalise Traffic | Converts raw HTTP traffic into structured JSON → `proxy_snapshot.json` |
| **Step 3** | Passive Analysis (7 parallel checks) | Spawns subagents that analyse the captured traffic without sending new requests |
| **Step 4** | Active Testing (3 parallel checks) | Spawns subagents that send real HTTP requests via Burp MCP to test for vulnerabilities |
| **Step 5** | Aggregate Findings | Collects all results into `security_audit.md` with severity counts |
| **Step 6** | Cleanup | Removes temporary scripts and processing files |

---

## 7. Understanding the Output

After the agent completes, the output folder will contain:

```
security_review_20260331_143012/
├── proxy_snapshot.json          # Normalised HTTP traffic (structured JSON)
├── findings_recon.md            # Host/subdomain enumeration results
├── findings_headers.md          # Security header audit results
├── findings_fingerprint.md      # Information leakage findings
├── findings_cors.md             # CORS policy analysis
├── findings_cookie_flags.md     # Cookie flag validation
├── findings_cookie_expiry.md    # Cookie expiration checks
├── findings_https.md            # HTTPS enforcement results
├── findings_idor.md             # IDOR test results
├── findings_sqli_json.md        # SQL injection test results
├── findings_numeric_boundary.md # Numeric boundary test results
└── security_audit.md            # High-level summary report
```

### `security_audit.md` — Summary Report

Contains a high-level severity overview:

```
| Severity   | Count |
|------------|-------|
| **High**   | n     |
| **Medium** | n     |
| **Low**    | n     |
| **Total**  | n     |
```

Plus per-module summaries (2–4 lines each) showing key metrics, issue counts, and severity breakdowns.

### `findings_*.md` — Detailed Findings

Each file contains:

- Specific vulnerabilities found with evidence (request/response data)
- Severity rating (High / Medium / Low)
- Remediation recommendations
- Affected endpoints and parameters

> **Start with `security_audit.md`** for the overall picture, then drill into specific `findings_*.md` files for evidence and remediation details.

---

## 8. Troubleshooting

### Burp Suite extension fails to load

- Verify Java version: `java --version` (JDK 21 recommended)
- Ensure the JAR path points to `build/libs/burp-mcp-all.jar` (not the proxy JAR)
- Check the Burp Suite **Errors** sub-tab under Extensions for detailed error messages

### MCP connection fails in VS Code

- Ensure Burp Suite is running **with the MCP extension loaded and active**
- Verify the SSE endpoint is accessible: `curl http://127.0.0.1:9876` should return a response
- Check that the `java` path in `mcp.json` is correct: run the full path with `--version` to confirm
- Check that the `mcp-proxy-all.jar` path is correct and the file exists
- Restart VS Code after updating `mcp.json`

### No proxy history appears

- You must use Burp's **built-in browser** (Proxy → Open browser), not your system browser
- Verify your traffic is routed through the proxy — check the Proxy tab for intercepted requests
- If using HTTPS targets, ensure Burp's CA certificate is trusted by the built-in browser (this is automatic for the built-in browser)

### Agent can't find MCP tools

- Verify `.vscode/mcp.json` is correctly formatted (valid JSON)
- Ensure the `burpsuite-mcp` server name matches what's expected
- Reload the VS Code window (`Cmd+Shift+P` → "Developer: Reload Window")
- Check VS Code's Output panel for MCP-related error messages

### Agent reports "no in-scope traffic"

- Verify the scope you provided matches the domains in your proxy history
- Example: if proxy history shows `shop-cert.deere.com` but you provided scope `*.example.com`, no traffic will match
- Check Proxy → HTTP history in Burp Suite to confirm traffic exists for your target domain

---

## 9. References

- [Burp Suite MCP Server — GitHub Repository](https://github.com/PortSwigger/mcp-server)
- [Burp Suite MCP Server — Installation Guide](https://github.com/PortSwigger/mcp-server?tab=readme-ov-file#installation)
- [Burp Suite Community Edition — Download](https://portswigger.net/burp/communitydownload)
- Security reviewer agent definition: [.github/agents/security-reviewer.md](.github/agents/security-reviewer.md)
- Subagent instructions: [.github/agents/security-review-instructions/](.github/agents/security-review-instructions/)
