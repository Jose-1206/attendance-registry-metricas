# 05 — Module Structure

## The tree

Attendance is organized as a Java/Spring Boot modular monolith application.

The module structure follows the architectural decision documented in:

[`./decisions/records/ADR-002-attendance-module-boundaries.md`](./decisions/records/ADR-002-attendance-module-boundaries.md)

The defined internal modules are:

- `identity`
- `academic`
- `class-sessions`
- `attendance`

These modules represent internal boundaries inside the same backend application. They are not independent services. Each module owns its responsibilities and communicates with other modules only through public APIs.

The module ownership is defined in:

[`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md)

The bounded contexts that originated these modules are defined in:

[`../02-domain/domain-map.md`](../02-domain/domain-map.md)

```
src/
├── main/
│   ├── java/
│   │   └── attendance/
│   │       ├── identity/
│   │       │   ├── api/
│   │       │   │   └── contracts/
│   │       │   └── internal/
│   │       │       ├── domain/
│   │       │       ├── application/
│   │       │       └── persistence/
│   │       │
│   │       ├── academic/
│   │       │   ├── api/
│   │       │   │   └── contracts/
│   │       │   └── internal/
│   │       │       ├── domain/
│   │       │       ├── application/
│   │       │       └── persistence/
│   │       │
│   │       ├── classsessions/
│   │       │   ├── api/
│   │       │   │   └── contracts/
│   │       │   └── internal/
│   │       │       ├── domain/
│   │       │       ├── application/
│   │       │       └── persistence/
│   │       │
│   │       ├── attendance/
│   │       │   ├── api/
│   │       │   │   └── contracts/
│   │       │   └── internal/
│   │       │       ├── domain/
│   │       │       ├── application/
│   │       │       └── persistence/
│   │       │
│   │       └── platform/
│   │           ├── config/
│   │           └── errors/
│   │
│   └── resources/
│       ├── application.properties
│       └── db/
│           └── migration/
│
└── test/
    └── java/
        ├── unit/
        ├── module-integration/
        └── end-to-end/
```

The logical module name `class-sessions` is represented as `classsessions` inside Java packages because Java package names cannot contain hyphens.

---

## What each folder holds

| Folder | Contains | Never contains |
|---|---|---|
| `<module>/api/` | Public contracts that other modules are allowed to use | Internal business rules or persistence implementation |
| `<module>/api/contracts/` | Request and response objects shared between module boundaries | Module-specific implementation details |
| `<module>/internal/domain/` | Entities, value objects, and business rules | Controllers or database behavior |
| `<module>/internal/application/` | Use cases, orchestration, and transactions | Direct HTTP concerns |
| `<module>/internal/persistence/` | Repository and database access implementation | Business decisions |
| `platform/config/` | Spring configuration and technical wiring | Attendance business rules |
| `platform/errors/` | Shared error handling | Module-specific business behavior |
| `resources/db/migration/` | Database schema migrations | Manual undocumented changes |
| `test/java/unit/` | Unit tests for domain and application rules | Complete application flows |
| `test/java/module-integration/` | Tests for module public boundaries | Direct internal access to another module |
| `test/java/end-to-end/` | Complete Attendance workflows | Only isolated internal classes |

---

## Module responsibilities

| Module | Responsibility |
|---|---|
| `identity` | User accounts, authentication, JWT access, and student/teacher roles |
| `academic` | Courses and enrollment management |
| `class-sessions` | Class sessions and temporary QR codes |
| `attendance` | Student check-ins, duplicate prevention, attendance history, and teacher review |

The module separation represents business responsibilities inside one backend application.

The modules do not represent independent database services. Database ownership and persistence decisions remain part of the application architecture.

---

## Rules that survive any stack

| Rule | Reason |
|---|---|
| A module exposes only its public API | Other modules should not depend on implementation details |
| `internal/` is never imported from another module | Preserves module isolation |
| Objects crossing module boundaries should be immutable | Prevents shared mutable state |
| Persistence belongs to the module that owns the data | Prevents direct modification of another module's data |
| Tests should respect module boundaries | Architecture rules must also apply to tests |
| Database decisions must follow the selected deployment architecture | Prevents incorrect assumptions about ownership |

---

## Attendance example

A QR attendance registration requires information from different modules:

```
attendance
    |
    +----> classsessions/api
    |
    +----> identity/api
```

The `attendance` module can use public APIs exposed by other modules.

It must not access:

```
classsessions/internal/

identity/internal/
```

For example, when a student scans a QR code, the attendance module requests class-session validation through the public API instead of directly accessing repositories or internal domain classes.

These restrictions are documented in:

[`./boundary-enforcement.md`](./boundary-enforcement.md)

---

## Relationship with layered architecture

Each Attendance module follows the layered responsibilities defined in:

[`./layered-architecture.md`](./layered-architecture.md)

Example:

```
attendance/
├── api/
│   └── contracts/
└── internal/
    ├── domain/
    ├── application/
    └── persistence/
```

Modules separate business responsibilities, while internal folders separate technical responsibilities.

The concrete Java/Spring conventions are documented in:

[`../_stacks/README.md`](../_stacks/README.md)

---

**Related:**  
[`./modular-monolith.md`](./modular-monolith.md) ·  
[`./boundary-enforcement.md`](./boundary-enforcement.md) ·  
[`./layered-architecture.md`](./layered-architecture.md)
