# ADR-002 — Attendance Module Boundaries

## Identification

| Field | Value |
|---|---|
| Id | ADR-002 |
| Date | 2026-09-18 |
| Status | Accepted |
| Authors | Attendance development team |
| Supersedes | none |

## Context

Attendance requires clear ownership of user information, courses and
enrollments, class sessions and QR codes, and attendance records. These
responsibilities participate in the same attendance workflow but represent
different business concepts and own different data.

The domain map identifies Identity, Academic, Class Sessions, and Attendance as
separate bounded contexts. A clear architectural decision is required because
changing these boundaries later would require moving data, application logic,
public APIs, tests, and dependencies between modules.

The decision also clarifies why Attendance depends on the other modules and why
QR codes belong to Class Sessions rather than Attendance.

The decision is supported by the following requirements and domain rules:

| Constraint | Source |
|---|---|
| A student must be enrolled in the corresponding course before attendance can be recorded | [`BR-06`](../../../02-domain/entities-and-rules.md#business-rules) |
| A QR code must belong to a class session and must not be expired | [`BR-05`](../../../02-domain/entities-and-rules.md#business-rules) |
| A student can register attendance only once for the same class session | [`BR-07`](../../../02-domain/entities-and-rules.md#business-rules) |
| The check-in time is stored when attendance is successfully registered | [`BR-08`](../../../02-domain/entities-and-rules.md#business-rules) |
| Domain responsibilities must have clear ownership and module boundaries | [`../../../02-domain/domain-map.md`](../../../02-domain/domain-map.md) |

## Traceability

This decision is related to the Attendance requirements and user needs defined
during product and domain analysis.

| Source | Purpose |
|---|---|
| User stories | Define the main attendance workflows for students and teachers, including QR attendance registration and attendance history. |
| [`../04-requirements`](../../04-requirements) | Defines functional requirements related to authentication, courses, QR generation, and attendance management. |
| [`../../../02-domain/domain-map.md`](../../../02-domain/domain-map.md) | Defines the bounded contexts and their relationships. |

## Decision

**We decided:** organize Attendance into four internal modules:

- `identity`
- `academic`
- `class-sessions`
- `attendance`

Each module owns its own data and exposes its functionality through a public
API.

The modules are internal boundaries of the Attendance application and are not
independent services. The separation follows business responsibility and data
ownership rather than technical layers.

The `attendance` module may reference Identity to identify the student,
Academic to validate course enrollment, and Class Sessions to validate the
active session and QR code. It does not write data owned by those modules.

QR codes are owned by `class-sessions` because a QR code represents temporary
access to a specific class session. The `attendance` module only validates that
QR code before creating its own attendance record.

## Alternatives evaluated

| Alternative | Pros | Cons | Verdict |
|---|---|---|---|
| Four internal modules: Identity, Academic, Class Sessions, and Attendance | Clear data ownership; matches the domain map; attendance validations have explicit dependencies | Requires public APIs and boundary enforcement between four modules | Chosen |
| Combine Academic and Class Sessions into one module | Fewer modules and simpler initial structure | Mixes course/enrollment management with session and QR lifecycle | Rejected |
| Put QR codes inside Attendance | Keeps QR validation near attendance registration | QR lifecycle starts when a class session is created, before attendance exists | Rejected |
| Use one flat application module | Simplest initial folder structure | Business boundaries and ownership become unclear as the application grows | Rejected |

## Consequences

| Kind | Consequence |
|---|---|
| Positive | Each business concept has one clear owner for its data and rules |
| Positive | Attendance validates identity, enrollment, and sessions without modifying external module data |
| Cost | Public APIs and dependency rules must be maintained between modules |
| Cost | Changes involving multiple business responsibilities require coordination |
| Risk | Modules may become tightly coupled if public APIs grow unnecessarily |

**Documents to update:**

- `02-domain/domain-map.md`
- `02-domain/module-boundaries.md`
- `05-architecture/boundary-enforcement.md`

## Revisiting

Reopen this decision if a circular dependency appears between these modules,
if a module requires direct access to another module's internal data, or if a
public facade grows beyond the responsibilities assigned to its bounded context.

---

**Related:** [`../README.md`](../README.md) · [`../../modular-monolith.md`](../../modular-monolith.md)
