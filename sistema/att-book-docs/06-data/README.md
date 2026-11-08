# 06 — Data

## Purpose

This section defines the data structure and persistence strategy of the Attendance system.

The Attendance application allows university students to register their attendance by scanning QR codes generated during classes. The data layer is responsible for storing users, roles, courses, class sessions, QR codes, and attendance records while maintaining consistency, security, and traceability of attendance information.

This section documents the database model, naming conventions, and schema versioning rules used by the application.

## Documents

| Document | Answers | Priority |
|---|---|---|
| [models.md](./models.md) | What are the main entities of the Attendance system and how are they related? Which module owns each table? | ⭐ |
| [database-conventions.md](./database-conventions.md) | How are tables, columns, relationships, keys, and indexes named and structured? | ⭐ |
| [migrations.md](./migrations.md) | How is the database schema versioned and how are changes managed? | ⭐ |

## Data Scope

The Attendance database supports the following main areas:

- User management:
  - Students and teachers.
  - Authentication and role information.

- Academic management:
  - Courses created by teachers.
  - Class sessions associated with each course.

- QR attendance management:
  - QR codes generated for each class session.
  - QR expiration control.

- Attendance management:
  - Student check-in records.
  - Attendance history.
  - Teacher review of attendance information.

## Data Ownership

The system follows a modular monolith approach with a single database.

Each module is responsible for the data related to its own business rules:

| Module | Data Responsibility |
|---|---|
| Auth | Authentication and access control information |
| Usuarios | Student and teacher information |
| Cursos | Course information and relationships |
| Clases | Class sessions and scheduling information |
| QR | QR generation and validation data |
| Asistencia | Attendance registration and history |

## Out of Scope

The following are not included in the current data design:

- Multiple independent databases.
- Database-per-service architecture.
- Distributed transactions.
- Data synchronization between different storage systems.

The project uses a single relational database managed by PostgreSQL, following the modular monolith architecture defined for Attendance.

## Ready when

- [ ] The data model represents the current Attendance functionalities.
- [ ] Every table has an identified module owner.
- [ ] Database naming conventions are documented.
- [ ] Schema changes follow the migration rules.
- [ ] Data documentation remains synchronized with the application implementation.

---

**Related:** [`../05-architecture/README.md`](../05-architecture/README.md) · [`../02-domain/module-boundaries.md`](../02-domain/module-boundaries.md) · [`../09-modules/README.md`](../09-modules/README.md)
