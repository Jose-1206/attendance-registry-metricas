# 04 — Non-Functional Requirements

## What makes a requirement valid

A non-functional requirement must define a measurable quality attribute and
how that attribute will be verified.

| Not this | This |
|---|---|
| The system must be fast | The defined API response-time threshold is measured under the specified load. |
| The system must be secure | Protected endpoints reject unauthenticated and unauthorized requests according to the defined tests. |
| The system must be reliable | The defined availability target is measured using application health checks. |

## Requirements

| NFR-NN | Category | Threshold | How it is measured |
|---|---|---|---|
| NFR-01 | Performance | p95 API response time under 800 ms at 50 concurrent authenticated users for the main course, session, QR, and attendance endpoints | Scripted load test against the target environment |
| NFR-02 | Performance | At least 95% of valid QR check-ins receive a success or rejection response within 2 seconds after submission | End-to-end timing test under the defined concurrent load |
| NFR-03 | Availability | At least 99.5% monthly backend availability, excluding announced maintenance windows | Uptime monitoring of the application health endpoint |
| NFR-04 | Security | 100% of protected endpoints reject requests with a missing, invalid, or expired JWT | Automated authentication tests |
| NFR-05 | Authorization | 100% of role-restricted operations reject authenticated users whose role is not allowed | Automated authorization tests for student and teacher roles |
| NFR-06 | Data integrity | Zero duplicate attendance records for the same student and class session, and 100% of invalid or expired QR attempts are rejected | Persistence and attendance integration tests |
| NFR-07 | Dependency security | Zero critical- or high-severity dependency findings are merged without a documented and expiring waiver | Dependency scanning according to the security policy |
| NFR-08 | Maintainability | At least 80% automated test coverage on changed backend files | CI coverage report |
| NFR-09 | Observability | 100% of unhandled backend errors are logged with timestamp, request path, and request or correlation id | Error-path test and log review |
| NFR-10 | Usability | At least 90% of first-time test users complete the student QR check-in flow in 30 seconds or less without assistance | Moderated usability test with at least 10 representative users |

## Requirement status

The thresholds above are requirement targets. Their presence in this document
does not by itself mean that the target has already been demonstrated.

Verification evidence belongs in the quality and testing documentation.

## Identifier format

`NFR-NN` uses a two-digit, sequential, never-reused identifier.

Changes to an NFR threshold should be made through a reviewed documentation
change so that related tests and traceability remain aligned.

---

**Related:** [`./user-stories.md`](./user-stories.md) · [`./traceability-matrix.md`](./traceability-matrix.md)
