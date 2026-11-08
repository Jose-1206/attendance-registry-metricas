# 01 — Context

## Purpose

This section defines why the Attendance system exists, who uses it, what
problem it addresses, and which capabilities are inside or outside the
current product scope.

The context is based on the Attendance application, which allows university
students to register attendance through QR codes and allows teachers to manage
courses, class sessions, and attendance information.

## Documents

| Document | Answers | Priority |
|---|---|---|
| [overview.md](./overview.md) | What is the Attendance system and who uses it? | ⭐ |
| [scope.md](./scope.md) | What is included and excluded from the current MVP? | ⭐ |
| [glossary.md](./glossary.md) | What do the main Attendance domain terms mean? | — |

## Context boundaries

The Attendance application focuses on attendance management for university
students and teachers.

The current scope includes authentication, role-based access, course
management, class sessions, temporary QR codes, attendance registration,
attendance history, and teacher attendance review.

The context does not define implementation details such as database schemas,
API contracts, module internals, or deployment infrastructure. Those concerns
are documented in their corresponding sections.

## Ready when

- [x] `overview.md` describes the real Attendance application.
- [x] `scope.md` identifies the current in-scope and out-of-scope capabilities.
- [x] `glossary.md` defines the main terms used by the domain documentation.
- [x] The documents use the same terminology as `02-domain` and `04-requirements`.

---

**Related:** [`../00-governance/README.md`](../00-governance/README.md) · [`../02-domain/README.md`](../02-domain/README.md)
