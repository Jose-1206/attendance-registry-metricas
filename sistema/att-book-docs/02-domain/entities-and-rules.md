# 02 — Entities and Rules

## Entities

| Entity | Key attributes | Invariants |
|---|---|---|
| User | name, email, password hash, role | Email is unique; role must be either `STUDENT` or `TEACHER`; passwords are never stored in plain text. Related rules: **BR-01**, **BR-02**. |
| Course | name, code, teacher reference | Every course must be associated with a registered teacher. Related rule: **BR-01**. |
| Enrollment | student reference, course reference, enrollment date | A student can be enrolled only once in the same course. Related rules: **BR-02**, **BR-03**. |
| ClassSession | course reference, date, start time, status | Every class session belongs to exactly one existing course. Related rule: **BR-04**. |
| QRCode | session reference, token, expiration time, active | The token is unique; it belongs to one class session and cannot be used after expiration. Related rule: **BR-05**. |
| AttendanceRecord | student reference, session reference, check-in time | A student can have only one attendance record per class session. The student must be enrolled before attendance is recorded. Related rules: **BR-06**, **BR-07**, **BR-08**. |

## Business rules

The following rules define the main business constraints of Attendance. They are aligned with the bounded contexts defined in [`domain-map.md`](./domain-map.md) and the module ownership decision documented in [`ADR-002 — Attendance Module Boundaries`](
../05-architecture/decisions/records/ADR-002-attendance-module-boundaries.md
).

1. **BR-01** — Only users with the `TEACHER` role can create and manage courses.

2. **BR-02** — Only users with the `STUDENT` role can be enrolled in a course.

3. **BR-03** — A student cannot be enrolled more than once in the same course.

4. **BR-04** — A class session can only be created for an existing course.

5. **BR-05** — A QR code is valid only for the class session for which it was generated and before its expiration time.

6. **BR-06** — A student can record attendance only if they are enrolled in the course associated with the class session.

7. **BR-07** — A student cannot register attendance more than once for the same class session.

8. **BR-08** — The attendance check-in time is recorded when a valid QR code is successfully scanned.

## Modeling decisions

The following decisions explain non-obvious choices made in the domain model:

| Decision | Reason |
|---|---|
| Enrollment includes `enrollment date` | The system needs to know when a student became associated with a course, supporting future attendance analysis and academic tracking. |
| ClassSession includes `status` | A class session may have different states during its lifecycle, such as created, active, or completed. |
| QRCode belongs to ClassSession | A QR code represents temporary access to a specific class session, therefore its lifecycle depends on the session where it was generated. |
| AttendanceRecord references the student and session | Attendance is an event that connects a student with a specific class session and stores the check-in moment. |

## Traceability

The domain model is supported by:

| Source | Purpose |
|---|---|
| [`domain-map.md`](./domain-map.md) | Defines the bounded contexts and relationships between Identity, Academic, Class Sessions, and Attendance. |
| [`ADR-002 — Attendance Module Boundaries`](../05-architecture/decisions/records/ADR-002-attendance-module-boundaries.md) | Defines the architectural decision behind the module separation and ownership responsibilities. |

## Where a rule is enforced

Every `BR-NN` rule lives in the module that owns the entity it governs — see
[`module-boundaries.md`](./module-boundaries.md) for which module that is.

A rule enforced in two modules is a sign that the boundary is in the wrong place.

---

**Related:** [`./domain-map.md`](./domain-map.md) · [`./module-boundaries.md`](./module-boundaries.md)
