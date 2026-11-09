# 13 — Observability

## Purpose

Observability supports investigation of errors and verification of the
non-functional requirements already documented for Attendance Registry.

This document describes logging, metrics, and health-check targets.
Their implementation requires configuration and execution evidence.

## Document status

The inherited framework examples concerning products, invoices, and billing
do not describe Attendance Registry.

No log aggregator, metrics dashboard, monitoring service, or automated alert
is declared operational solely by its inclusion in this document.

## Logging target

The documented structured logging target includes:

| Field | Purpose |
|---|---|
| `timestamp` | Identify when the event occurred, using an unambiguous timestamp. |
| `level` | Distinguish informational, warning, and error events. |
| `correlationId` | Associate events belonging to the same request. |
| `module` | Identify the responsible application module. |
| `message` | Describe the event without exposing sensitive information. |

The documented logical modules are:

- `identity`
- `academic`
- `class-sessions`
- `attendance`

These names describe domain responsibilities. They do not imply separate
processes, monitoring services, or physical database schemas.

The presence and format of these fields must be checked against actual logs.

## Error traceability

NFR-09 requires unhandled backend errors to be logged with:

- Timestamp.
- Request path.
- Request or correlation identifier.

A stack trace alone does not demonstrate compliance with NFR-09.

A request identifier should allow related events to be located during
investigation. Its generation and propagation require verification.

## Sensitive information

Diagnostic information must not expose passwords, password hashes,
authentication tokens, or deployment secrets.

Logs shared as evidence must be reviewed for sensitive information.

## Metrics targets

| Proposed metric | Purpose | Requirement reference |
|---|---|---|
| `http_requests_total` | Observe request counts and response status distribution. | Supports investigation of service behavior. |
| `http_request_duration_seconds` | Measure API response-time distribution. | NFR-01. |

Metric names are proposed instrumentation names.
They do not establish that an exporter or metrics collector is configured.

NFR-01 defines a p95 response time below 800 ms at 50 concurrent authenticated
users for the specified main endpoints.

Availability measurement follows NFR-03:
at least 99.5% monthly backend availability, excluding announced maintenance.

These thresholds are targets until measurement evidence is available.

## Health-check interpretation

A health-check result must be interpreted according to what its handler
actually verifies.

A response from the web server does not, by itself, demonstrate that the API
and database are available.

An application readiness check must establish the required dependencies
before it is used as evidence of complete service readiness.

The health-check endpoint, monitoring configuration, and measured availability
must be verified before reporting NFR-03 as satisfied.

## Investigation signals

| Signal | Investigation |
|---|---|
| Authentication errors | Review authentication-related events without exposing credentials or tokens. |
| Authorization failures | Check the authenticated role and applicable resource ownership rule. |
| Attendance rejection | Compare the result with the documented QR, enrollment, and duplicate-prevention rules. |
| Unhandled backend errors | Locate the request using its timestamp, path, and identifier. |
| Increased response time | Compare measured results with NFR-01 under the specified load. |
| Service unavailable | Check the affected service and its dependencies using the operational procedures. |

These are investigation directions.
They do not claim that automated detection or alerting is implemented.

## Verification evidence

Before declaring an observability capability operational, retain the relevant
configuration and execution evidence.

Examples include:

- A reviewed log sample containing the required fields.
- A measurement report identifying the tested environment and load.
- A health-check result with a clear description of what it verifies.

NFR-10 concerns usability of the student QR attendance flow.
It does not define invoice support or incident resolution time.

## Related documents

- [Runbook](./runbook.md)
- [Non-functional requirements](../04-requirements/non-functional.md)
- [Module boundaries](../02-domain/module-boundaries.md)
