# 06 — Database Conventions

## One database, divided by module

The Attendance project uses a single PostgreSQL database following the modular monolith architecture.

The database is organized by module ownership, where each module is responsible for its own data and business rules.

The selected approach is **schema per module**, allowing each module to have its own database namespace while maintaining a single database instance.

The four application modules are:

- `identity`
- `academic`
- `class-sessions`
- `attendance`

| Mechanism | How it works | Reviewer check |
|---|---|---|
| Schema per module | Each module owns a database schema containing its tables and data structures | Verify that the physical schema and its tables correspond to the responsible application module |
| Table prefix per module (fallback) | Tables use module-specific prefixes when schema separation is unavailable | Validate table names and ownership against the approved module boundaries |

The project adopts schema separation to provide clearer ownership boundaries and maintain consistency with the modular monolith architecture.

Physical PostgreSQL schema names and table names must be confirmed against the backend persistence model and database migrations before being documented as implemented structures.

## Module data ownership

Each module owns and manages its corresponding data:

| Module | Data responsibility |
|---|---|
| `identity` | User accounts, authentication information, and access control |
| `academic` | Courses and student enrollments |
| `class-sessions` | Class sessions and QR code information |
| `attendance` | Attendance records and student attendance history |

A module must not directly modify another module's data.

Communication between modules must follow the public interfaces and application services defined in the architecture documentation.

Module ownership must remain aligned with [`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md).

## Relationships between modules

Foreign keys should only be created inside the same module boundary.

When a module needs information owned by another module, the reference should be stored as an identifier and validated through the responsible module.

Example:

- The `attendance` module stores a student identifier and a class session identifier.
- The `identity` module remains responsible for student account information.
- The `class-sessions` module remains responsible for class session information.
- The `academic` module remains responsible for course enrollment information.
- Attendance validation must respect the corresponding module boundaries and business rules.

This approach keeps data ownership clear and reduces coupling between modules.

---

# Naming

## Database elements

| Element | Rule | Example |
|---|---|---|
| Entity | Singular name representing a business concept | User, Course, Enrollment |
| Table | `snake_case` and plural | users, courses, enrollments |
| Column | `snake_case` | created_at, course_id |
| Primary key | UUID with column name `id` | id UUID PRIMARY KEY |
| Foreign key | Referenced entity name followed by `_id` | student_id, course_id |
| Boolean | Prefix with `is_` or `has_` | is_active |
| Index | `idx_<table>_<column>` | idx_enrollments_student_id |

The examples illustrate naming conventions and do not confirm the physical database schema.

---

# Dates and audit columns

All tables must include audit information to track record creation and updates.

Required columns:

| Column | Type | Description |
|---|---|---|
| created_at | TIMESTAMPTZ | Date and time when the record was created |
| updated_at | TIMESTAMPTZ | Date and time when the record was updated |

Example:

```sql
created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
```

The application or database must ensure that `updated_at` is refreshed when a record changes.

---

**Related:** [`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md) · [`../05-architecture/modular-monolith.md`](../05-architecture/modular-monolith.md) · [`./models.md`](./models.md) · [`./migrations.md`](./migrations.md)
