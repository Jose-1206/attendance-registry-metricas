# 06 — Migrations

## One sequence for the whole application

Attendance Registry uses a single PostgreSQL database shared by its modules.

Each module owns its domain data, while database changes follow one ordered
migration history for the application.

| Rule | Detail |
|---|---|
| Migration location | SQL files in the `migrations` directory of `att-book-db`. |
| Execution in the supplied deployment | PostgreSQL container initialization executes the mounted SQL scripts when its data directory is empty. |
| Sequence | One ordered migration history for the complete database. |
| Migration scope | Each migration represents one logical database change. |
| Module reference | Each migration identifies its purpose and responsible domain area. |

Maven builds the backend. It is not the database migration execution
mechanism in the supplied deployment.

## Module ownership

| Module | Database change responsibility |
|---|---|
| `identity` | User accounts and role-related data. |
| `academic` | Courses and student enrollments. |
| `class-sessions` | Class sessions and QR codes. |
| `attendance` | Attendance records. |

Changes affecting another module's data require coordination with its owner.

## Migration rules

- Every migration receives a unique version number.
- Executed migrations must not be modified.
- New changes must be added through new migration files.
- Each migration must have a clear purpose.
- Changes must remain consistent with domain ownership.
- Backend persistence mappings must remain compatible with the database.

Versioned filenames establish ordering. They do not, by themselves,
provide automatic migration tracking or execution on an existing database.

## Initialization of a new database

The infrastructure configuration mounts the migration directory into
PostgreSQL's container initialization directory.

When PostgreSQL starts with an empty data directory, it executes the
initialization scripts.

The backend uses:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Hibernate validates the existing schema instead of creating or updating it.

Successful container startup alone does not establish that the entire
application workflow has been verified.

## Existing databases

Initialization scripts are not automatically executed again when PostgreSQL
starts with an existing database volume.

Adding a new SQL migration file therefore does not automatically update
an existing installation.

Before applying changes to an existing database:

1. Identify the current database structure and previously applied changes.
2. Review the new migration and its compatibility with existing data.
3. Preserve the database through the appropriate backup procedure.
4. Apply the pending changes through a reviewed execution procedure.
5. Verify the resulting schema and affected application behavior.
6. Record the applied migration and execution result.

Do not remove a database volume as a routine migration procedure.

## Development seeds

Development seeds are separate from schema migrations.

They provide demonstration data and must be loaded according to the
instructions in `att-book-db` and `att-book-infra`.

Do not assume that starting the database also loads the development seeds.

## Rollback policy

Database changes must consider recovery from an incorrect migration.

| Change type | Rule |
|---|---|
| Additive changes | Review how the added structure could be reverted and whether it contains data that must be preserved. |
| Destructive changes | Assess dependencies, data loss, and recovery before execution. |
| Migration files | Document the applicable reversal or recovery procedure. |
| Application rollback | Check database compatibility before restoring an earlier application revision. |

Restoring an older application revision does not automatically reverse
database changes.

No automatic rollback mechanism is established by this document.

## Migration process

1. Create a new versioned migration file.
2. Define the database change.
3. Review its impact on the affected modules.
4. Execute it in the development environment using the applicable procedure.
5. Verify schema integrity and affected application behavior.
6. Record the result.
7. Apply the reviewed change to the next environment.

## Database consistency

Migration changes must preserve:

- Data integrity.
- Domain ownership boundaries.
- Attendance history.
- Compatibility between backend entities and database tables.

Changes must remain aligned with the documented Attendance requirements.

## Verification status

This document describes the migration rules and the supplied deployment
mechanism.

Successful initialization, migration execution, backup, and recovery require
their corresponding execution evidence.

## Related documents

- [Database conventions](./database-conventions.md)
- [Data models](./models.md)
- [Modular monolith](../05-architecture/modular-monolith.md)
- [Local setup](../10-devops/local-setup.md)
- [Database repository](https://github.com/code-corhuila/att-book-db)
