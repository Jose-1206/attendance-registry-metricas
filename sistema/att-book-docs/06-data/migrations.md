# 06 — Migrations

## One sequence for the whole application

The Attendance project uses a single PostgreSQL database shared by all modules.

Although each module owns its data, database changes follow one ordered migration history for the entire application.

This approach guarantees that the database structure remains synchronized with the application version and prevents conflicts between module changes.

| Rule | Detail |
|---|---|
| Migration tool | Maven database migration management |
| Sequence | One ordered migration history for the complete database |
| Migration scope | Each migration must represent one logical database change |
| Module reference | Each migration identifies the responsible module for traceability |

Examples of module ownership:

| Module | Migration example |
|---|---|
| `identity` | Create or modify user, authentication, and role-related tables |
| `academic` | Create or modify course and enrollment tables |
| `class-sessions` | Create or modify class session and QR code tables |
| `attendance` | Create or modify attendance record tables |

A migration must never modify another module's data without respecting ownership boundaries.

---

# Migration rules

## Changeset management

Database migrations must follow these rules:

- Every migration receives a unique version number.
- Executed migrations must never be modified.
- New changes must be added through new migration files.
- Each migration must have a clear purpose.
- Database changes must remain aligned with the application modules.


---

# Rollback policy

Database changes must consider how the system can recover from incorrect migrations.

| Change type | Rule |
|---|---|
| Additive changes | New tables, columns, or indexes should include a rollback strategy |
| Destructive changes | Remove or rename operations require previous validation before execution |
| Migration files | A migration must define how the change can be reverted when necessary |

Before applying destructive database changes, the impact on existing attendance records must be evaluated.

---

# Migration process

The migration process follows these steps:

1. Create a new migration file.
2. Define the database change.
3. Review the impact on the affected module.
4. Execute the migration in the development environment.
5. Validate that the application continues working correctly.
6. Apply the migration to the next environments.

---

# Database consistency

Migration changes must preserve:

- Data integrity.
- Module ownership boundaries.
- Attendance history records.
- Compatibility between backend entities and database tables.

The database evolution must remain synchronized with the Attendance application requirements.

---

**Related:**  
[`./database-conventions.md`](./database-conventions.md) ·  
[`../05-architecture/modular-monolith.md`](../05-architecture/modular-monolith.md)
