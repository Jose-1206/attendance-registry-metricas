# 09 — Module Catalog

## Purpose

This catalog summarizes the Attendance Registry modules already defined in
the domain and architecture documentation.

It replaces the unrelated catalog and billing examples inherited from
the documentation framework. It does not introduce additional scope.

## Documented modules

| Module | Responsibility | Owned domain entities |
|---|---|---|
| `identity` | User accounts, authentication, and roles. | User |
| `academic` | Courses and student enrollments. | Course, Enrollment |
| `class-sessions` | Class sessions and temporary QR codes. | ClassSession, QRCode |
| `attendance` | Attendance registration, personal history, and session attendance review. | AttendanceRecord |

## Ownership

Ownership follows the existing domain boundaries and logical data model.

Domain entity names do not establish physical PostgreSQL schemas or table
names. Physical mappings require comparison with persistence definitions.

Cross-module interactions must follow the architectural boundaries.
Listing a module here does not demonstrate that automated boundary checks
are configured or passing.

## Verification status

This catalog records documented responsibilities.

Implementation completeness, public interfaces, module runbooks, and
automated boundary enforcement require their corresponding evidence.
No stability classification is assigned without that review.

## Related documents

- [Module boundaries](../02-domain/module-boundaries.md)
- [Domain entities and rules](../02-domain/entities-and-rules.md)
- [Data models](../06-data/models.md)
- [Modular monolith](../05-architecture/modular-monolith.md)
