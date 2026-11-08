# 04 — User Stories

## Story identifier

`HU-NN` — sequential, two digits, starting at `01`. An identifier is never
reused, even if a story is later removed from the product scope.

Each story has one authoritative description in its individual file.
This document provides the backlog index and summary traceability.

## Backlog

| Story | Title | Module | Priority |
|---|---|---|---|
| [HU-01](./stories/HU-01.md) | Register a user account | `identity` | ⭐ |
| [HU-02](./stories/HU-02.md) | Log in to Attendance | `identity` | ⭐ |
| [HU-03](./stories/HU-03.md) | View my authenticated profile | `identity` | — |
| [HU-04](./stories/HU-04.md) | Create a course | `academic` | ⭐ |
| [HU-05](./stories/HU-05.md) | View my courses as a teacher | `academic` | ⭐ |
| [HU-06](./stories/HU-06.md) | Create a class session | `class-sessions` | ⭐ |
| [HU-07](./stories/HU-07.md) | View class sessions for a course | `class-sessions` | — |
| [HU-08](./stories/HU-08.md) | Generate a temporary QR code for a class session | `class-sessions` | ⭐ |
| [HU-09](./stories/HU-09.md) | Record attendance by scanning a QR code | `attendance` | ⭐ |
| [HU-10](./stories/HU-10.md) | View my attendance history as a student | `attendance` | ⭐ |
| [HU-11](./stories/HU-11.md) | Review attendance for a class session as a teacher | `attendance` | ⭐ |

The priority markers preserve the existing backlog. A priority marker does
not by itself confirm inclusion in the MVP or implementation status.

## MVP scope

Final MVP inclusion is pending confirmation against the scope coordinated
by Juan Jose Guzman Bohorquez in the [root README](../README.md).

After that agreement, this index must explicitly identify the included
and excluded story IDs. HU-11 also requires confirmation of the
session-attendance query with the API contract owner, Jose Miguel Aya Peralta.

Creating an individual story file does not mean that the story has been
approved for the MVP, implemented, or tested.

## Story intent

| Story group | User value |
|---|---|
| HU-01 to HU-03 | Provide authenticated users with the correct role-based access. |
| HU-04 to HU-05 | Allow teachers to create and find their courses. |
| HU-06 to HU-08 | Allow teachers to create and view class sessions and generate temporary QR codes. |
| HU-09 | Allow enrolled students to record attendance while invalid, expired, or duplicate attempts are rejected. |
| HU-10 | Allow students to review their own recorded attendance. |
| HU-11 | Allow teachers to review attendance records for their class sessions. |

## Business-rule traceability

| User story | Main business rules or invariants |
|---|---|
| [HU-01](./stories/HU-01.md) | `User` invariants: unique email, supported roles, and password hashing. |
| [HU-02](./stories/HU-02.md) | Authentication, supported roles, and password confidentiality. |
| [HU-03](./stories/HU-03.md) | Authenticated identity and password confidentiality. |
| [HU-04](./stories/HU-04.md) | BR-01. |
| [HU-05](./stories/HU-05.md) | BR-01; course-list ownership scope is specified in the story. |
| [HU-06](./stories/HU-06.md) | BR-04. |
| [HU-07](./stories/HU-07.md) | BR-04 as the course-session relationship; creation enforcement is covered by HU-06. |
| [HU-08](./stories/HU-08.md) | BR-05 and QR token uniqueness. |
| [HU-09](./stories/HU-09.md) | BR-05, BR-06, BR-07, BR-08. |
| [HU-10](./stories/HU-10.md) | BR-08 as the source of the displayed check-in time. |
| [HU-11](./stories/HU-11.md) | BR-08 as the source of the displayed check-in time. |

The authoritative definitions of BR-01 to BR-08 and the entity invariants
are maintained in [entities and rules](../02-domain/entities-and-rules.md).

## Writing a story in full

Use [_template-hu.md](./_template-hu.md) as the starting point for each
individual story. Keep the reusable template unchanged.

Individual story files belong in `stories/`. Their relative links must
account for that directory.

The module assigned to each story must match
[module boundaries](../02-domain/module-boundaries.md).

Acceptance criteria must describe observable behavior and reference the
applicable business rules or entity invariants.

Unchecked definition-of-ready items identify decisions that still require
confirmation. They are not evidence of implementation.

---

**Related:** [Story template](./_template-hu.md) · [Traceability matrix](./traceability-matrix.md) · [Non-functional requirements](./non-functional.md)
