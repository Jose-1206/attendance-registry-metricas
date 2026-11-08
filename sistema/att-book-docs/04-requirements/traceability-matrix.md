# 04 — Traceability Matrix

## Purpose

This matrix connects each Attendance Registry requirement with its
responsible module, design/domain documentation, and expected verification.

Each user-story identifier links to its authoritative individual file.

Verification descriptions are targets. They do not establish that a test
exists or has passed. Exact test-case identifiers and evidence links must
be added when available.

## User stories

| Story | Module | Design/domain document | Expected verification |
|---|---|---|---|
| [HU-01](./stories/HU-01.md) | `identity` | [Entities and rules](../02-domain/entities-and-rules.md) | Verify registration, unique email, supported roles, password hashing, and rejection of invalid input. |
| [HU-02](./stories/HU-02.md) | `identity` | [Module boundaries](../02-domain/module-boundaries.md) | Verify valid login, rejection of invalid credentials, JWT validation, and role-restricted access. |
| [HU-03](./stories/HU-03.md) | `identity` | [Module boundaries](../02-domain/module-boundaries.md) | Verify retrieval of the authenticated user's own profile, exclusion of password data, and rejection of unauthenticated requests. |
| [HU-04](./stories/HU-04.md) | `academic` | [Entities and rules](../02-domain/entities-and-rules.md) | Verify teacher course creation, teacher association, required-field validation, and access restrictions. |
| [HU-05](./stories/HU-05.md) | `academic` | [Module boundaries](../02-domain/module-boundaries.md) | Verify the teacher's own course list, empty results, and exclusion of other teachers' courses. |
| [HU-06](./stories/HU-06.md) | `class-sessions` | [Entities and rules](../02-domain/entities-and-rules.md) | Verify session creation for an existing course, required information, and teacher ownership restrictions. |
| [HU-07](./stories/HU-07.md) | `class-sessions` | [Module boundaries](../02-domain/module-boundaries.md) | Verify sessions for the selected course, empty results, nonexistent courses, and access restrictions. |
| [HU-08](./stories/HU-08.md) | `class-sessions` | [Entities and rules](../02-domain/entities-and-rules.md) | Verify unique QR tokens, session association, expiration assignment, and generation access restrictions. |
| [HU-09](./stories/HU-09.md) | `attendance` | [Entities and rules](../02-domain/entities-and-rules.md) | Verify QR validity, enrollment, duplicate prevention including concurrent submissions, and the recorded check-in time. |
| [HU-10](./stories/HU-10.md) | `attendance` | [Module boundaries](../02-domain/module-boundaries.md) | Verify personal attendance history, empty results, original timestamps, and access restrictions. |
| [HU-11](./stories/HU-11.md) | `attendance` | [Module boundaries](../02-domain/module-boundaries.md) | Verify attendance for the teacher's own session, empty results, original timestamps, and access restrictions. |

## Non-functional requirements

The authoritative thresholds and measurement methods are defined in
[non-functional requirements](./non-functional.md).

| Requirement | Scope | Design/quality document | Expected verification |
|---|---|---|---|
| NFR-01 | Main course, session, QR, and attendance endpoints | [Testing strategy](../11-quality/testing-strategy.md) | Measure p95 API latency under the defined concurrent load. |
| NFR-02 | `attendance` | [Entities and rules](../02-domain/entities-and-rules.md) | Measure QR check-in response time under the defined load. |
| NFR-03 | Whole backend | [Observability](../13-operations/observability.md) | Calculate availability from application health checks. |
| NFR-04 | `identity` and protected endpoints | [REST conventions](../07-api/rest-conventions.md) | Verify rejection of requests with missing, invalid, or expired JWTs. |
| NFR-05 | All role-restricted modules | [Module boundaries](../02-domain/module-boundaries.md) | Verify student and teacher authorization restrictions. |
| NFR-06 | `class-sessions`, `attendance` | [Entities and rules](../02-domain/entities-and-rules.md) | Verify QR validity and uniqueness of attendance per student and session. |
| NFR-07 | Whole project | [Security policy](../00-governance/security-policy.md) | Verify dependency scanning and the handling of findings according to the security policy. |
| NFR-08 | Backend | [Definition of done](../00-governance/definition-of-done.md) | Verify automated test coverage of changed backend files. |
| NFR-09 | Backend | [Observability](../13-operations/observability.md) | Verify required fields in unhandled-error logs. |
| NFR-10 | Student QR check-in flow in the web portal | [Product vision](../03-product/vision.md) | Measure completion time and unassisted completion rate using the defined usability test. |

Cross-cutting requirements apply according to their defined scope even
when they are not repeated in every individual story.

## Business-rule traceability

| Business rule | Related user stories | Coverage relationship |
|---|---|---|
| BR-01 | [HU-04](./stories/HU-04.md), [HU-05](./stories/HU-05.md) | Teacher course creation and management access. |
| BR-02 | No direct enrollment story in HU-01 to HU-11 | Requires enrollment-operation coverage. HU-09 consumes enrollment as a precondition but does not perform enrollment. |
| BR-03 | No direct enrollment story in HU-01 to HU-11 | Requires verification that enrollment cannot be duplicated. Attendance duplicate prevention is a separate rule, BR-07. |
| BR-04 | [HU-06](./stories/HU-06.md), [HU-07](./stories/HU-07.md) | HU-06 verifies the existing-course constraint; HU-07 reads the resulting course-session relationship. |
| BR-05 | [HU-08](./stories/HU-08.md), [HU-09](./stories/HU-09.md) | HU-08 establishes QR session association and expiration; HU-09 verifies validity during check-in. |
| BR-06 | [HU-09](./stories/HU-09.md) | Enrollment is required before attendance is recorded. |
| BR-07 | [HU-09](./stories/HU-09.md) | Duplicate attendance is rejected, including concurrent submissions. |
| BR-08 | [HU-09](./stories/HU-09.md), [HU-10](./stories/HU-10.md), [HU-11](./stories/HU-11.md) | HU-09 records the check-in time; HU-10 and HU-11 display the stored time without modifying it. |

The authoritative business-rule definitions and entity invariants are
maintained in [entities and rules](../02-domain/entities-and-rules.md).

HU-01 covers account registration and `User` invariants. It does not
enroll a student in a course and therefore does not directly cover BR-02
or BR-03.

## Current gaps and pending confirmations

### Test evidence

Exact test-case identifiers and execution evidence have not been verified
as part of this documentation update.

When available, add the exact test-case identifiers and evidence links
alongside the expected verification descriptions. Preserve requirement IDs
and the descriptions of what must be verified.

### MVP scope

Confirm the included story IDs against the scope coordinated by
Juan Jose Guzman Bohorquez in the [root README](../README.md), then update
the [user-story index](./user-stories.md).

Backlog priority does not establish MVP inclusion or implementation status.

### API alignment

Confirm story references against the final API contracts maintained by
Jose Miguel Aya Peralta.

In particular, HU-11 requires confirmation of the session-attendance query,
its teacher ownership restrictions, and its inclusion in the MVP.

This matrix does not claim that any API operation has been implemented
or tested.

### Enrollment coverage

BR-02 and BR-03 require explicit enrollment coverage that the current
HU-01 to HU-11 backlog does not directly provide.

The team must agree how enrollment fits the MVP and document the
corresponding story and verification if it is included. Existing story
identifiers must not be reused or renumbered.

---

**Related:** [User stories](./user-stories.md) · [Non-functional requirements](./non-functional.md) · [Entities and rules](../02-domain/entities-and-rules.md)
