# 05 — Layered Architecture

## The four layers

Attendance uses a layered architecture inside each domain module:
`identity`, `academic`, `class-sessions`, and `attendance`.

The layer structure follows the modular monolith decision defined in:

[`./decisions/records/ADR-002-attendance-module-boundaries.md`](./decisions/records/ADR-002-attendance-module-boundaries.md)

Each module contains four responsibilities:

| Layer | Responsibility | May depend on |
|---|---|---|
| Presentation | Receive requests, validate input, handle DTOs, delegate actions, and return responses | Application |
| Application | Coordinate use cases, transactions, and business workflows | Persistence, Domain |
| Persistence | Read and write stored data without making business decisions | Domain |
| Domain | Contains entities, invariants, and business rules | Nothing |

The Presentation layer can use transport objects such as DTOs because they represent communication data between the client and application. However, it must not bypass the Application layer and access Persistence or Domain logic directly.

For Attendance, these layers support the following workflows:

- User authentication.
- Course creation and enrollment management.
- Class session creation.
- QR code generation and validation.
- Attendance registration.
- Attendance history consultation.

The business rules related to these workflows are defined in:

[`../02-domain/entities-and-rules.md`](../02-domain/entities-and-rules.md)

The technical stack conventions are documented in:

[`../_stacks/README.md`](../_stacks/README.md)

---

## Dependency direction

The allowed dependency direction is:

```text
Presentation → Application → Persistence → Domain
```

| Rule | Why it holds |
|---|---|
| Dependencies point in one direction | Prevents circular dependencies between layers |
| Domain depends on nothing outside itself | Keeps business rules independent from frameworks and databases |
| Presentation does not call Persistence directly | Prevents bypassing application workflows |
| Application does not depend on HTTP-specific behavior | Keeps use cases independent from controllers |
| Persistence contains no business decisions | Repositories only manage data access |

Example:

When a student scans a QR code, the Presentation layer receives the request and delegates the operation to Application. The Application layer validates the required business rules before Persistence stores the attendance record.

The main rules involved are:

- `BR-05` — QR code must belong to a valid class session and cannot be expired.
- `BR-06` — Student must be enrolled in the corresponding course.
- `BR-07` — A student cannot register attendance more than once.
- `BR-08` — Check-in time is stored when attendance is successfully registered.

---

## Layers inside Attendance modules

Attendance contains multiple bounded contexts. Therefore, each module organizes its responsibilities using the structure defined in [`module-structure.md`](./module-structure.md).

The Presentation responsibility is handled through the module's public API and request-handling components. Application, Domain, and Persistence responsibilities are organized inside the module's `internal/` directory.

The documented Java package structure is:

```text
identity/
├── api/
│   └── contracts/
└── internal/
    ├── domain/
    ├── application/
    └── persistence/

academic/
├── api/
│   └── contracts/
└── internal/
    ├── domain/
    ├── application/
    └── persistence/

classsessions/
├── api/
│   └── contracts/
└── internal/
    ├── domain/
    ├── application/
    └── persistence/

attendance/
├── api/
│   └── contracts/
└── internal/
    ├── domain/
    ├── application/
    └── persistence/
```

The logical module name `class-sessions` is represented as `classsessions` in Java packages.

The module boundaries are defined in:

[`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md)

The dependency restrictions are defined in:

[`./boundary-enforcement.md`](./boundary-enforcement.md)

---



---

## When a new module is needed

A new module is created when a new bounded context is identified in:

[`../02-domain/domain-map.md`](../02-domain/domain-map.md)

| Signal | Attendance decision |
|---|---|
| Current domain modules | `identity`, `academic`, `class-sessions`, `attendance` |
| Architecture | Modular monolith with layers inside each module |
| Boundary rule | Modules communicate only through public APIs |

A new module must also define its responsibility in:

[`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md)

When the decision affects architecture boundaries, an ADR must be created.

---

## How the structure evolves

| Step | What changes |
|---|---|
| 1 | Add the new bounded context to `domain-map.md` |
| 2 | Define ownership in `module-boundaries.md` |
| 3 | Create the module with the same four layers |
| 4 | Expose only the required public API |
| 5 | Apply boundary verification rules |
| 6 | Document architectural decisions through ADRs when required |

The layered architecture remains consistent as Attendance grows because each module keeps responsibility over its own business rules and data.

---

**Related:**  
[`./modular-monolith.md`](./modular-monolith.md) ·  
[`./module-structure.md`](./module-structure.md) ·  
[`./boundary-enforcement.md`](./boundary-enforcement.md)
