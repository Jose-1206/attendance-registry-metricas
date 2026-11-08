# 02 — Module Boundaries

## What a module is

A **module** is a bounded context that lives inside the single deployable. It owns
its data, exposes a narrow **public API**, and keeps everything else **internal**.

The modules defined in this document are internal boundaries of the Attendance
modular monolith. They are not independent services or separate deployments.
Their purpose is to organize business responsibilities, data ownership, and
dependencies inside the same application.

| Term | Meaning | Enforced by |
|---|---|---|
| Module | A bounded context with its own folder and its own tables | Folder layout |
| Public API | The only entry point other modules may call | Boundary linter (see `05-architecture/boundary-enforcement.md`) |
| Internal | Everything else in the module | Boundary linter |

## How to draw the boundary

1. Group behaviour by the **language the business uses**, not by technical layer.
   `Identity`, `Academic`, `Class Sessions`, and `Attendance` are modules;
   `Controllers` and `Repositories` are not.

2. A module should own the data it writes. If two modules write the same table,
   the boundary is in the wrong place.

3. Prefer **fewer, larger** modules at the start. Splitting is cheap; merging is not.

The module separation decision is documented in:


[`ADR-002 — Attendance Module Boundaries`](../05-architecture/decisions/records/ADR-002-attendance-module-boundaries.md)


## Module map

| Module | Owns | Public API | Depends on |
|---|---|---|---|
| `identity` | `users` | `IdentityFacade` | — |
| `academic` | `courses`, `enrollments` | `AcademicFacade` | `identity` |
| `class-sessions` | `class_sessions`, `qr_codes` | `ClassSessionsFacade` | `academic` |
| `attendance` | `attendance_records` | `AttendanceFacade` | `identity`, `academic`, `class-sessions` |

## Module ownership and dependencies

### Identity

The `identity` module owns user information and authentication.

Other modules can reference user information through its public API, but they
cannot modify Identity-owned data.

---

### Academic

The `academic` module owns courses and enrollments.

It depends on `identity` because courses require registered teachers and
students.

Related business rules:

- **BR-01** — Only users with the `TEACHER` role can create and manage courses.
- **BR-02** — Only users with the `STUDENT` role can be enrolled in a course.
- **BR-03** — A student cannot be enrolled more than once in the same course.

---

### Class Sessions

The `class-sessions` module owns class sessions and QR codes.

QR codes belong to this module because they are created as part of a class
session lifecycle. The QR is temporary access for a specific session and is not
an attendance record.

Related business rules:

- **BR-04** — A class session can only be created for an existing course.
- **BR-05** — A QR code is valid only for the session where it was generated and before expiration.

---

### Attendance

The `attendance` module owns attendance records.

It depends on:

- `identity` to identify the student.
- `academic` to validate enrollment.
- `class-sessions` to validate the session and QR code.

Attendance does not write or modify data owned by other modules.

Related business rules:

- **BR-06** — A student can record attendance only if they are enrolled in the course.
- **BR-07** — A student cannot register attendance more than once for the same class session.
- **BR-08** — The check-in time is recorded after successful attendance registration.

## Traceability

The module boundaries are based on:

| Source | Purpose |
|---|---|
| [`domain-map.md`](./domain-map.md) | Defines the bounded contexts and relationships between them. |
| [`entities-and-rules.md`](./entities-and-rules.md) | Defines entities and business rules associated with each context. |
| [`ADR-002 — Attendance Module Boundaries`](../05-architecture/decisions/records/ADR-002-attendance-module-boundaries.md) | Documents why these module boundaries were selected. |

## Signals the boundary is wrong

| Signal | What it means |
|---|---|
| Every feature touches three modules | The boundary cuts across a real workflow |
| Two modules write the same table | Ownership is not settled |
| A module's public API has 20 methods | It is a layer, not a module |
| Circular dependency between modules | One of them should not exist |

---

**Related:** [`../05-architecture/modular-monolith.md`](../05-architecture/modular-monolith.md) · [`../09-modules/module-catalog.md`](../09-modules/module-catalog.md)
