# Development Identity Seed

## Purpose

`identity.sql` creates the four demonstration accounts referenced by
`academic.json`.

This seed is exclusively for development and demonstration environments.

## Accounts

Shared demonstration password: `AttendanceDemo123!`

| Academic reference | Email | Role |
|---|---|---|
| `teacher_owner` | `teacher.owner@example.test` | `TEACHER` |
| `teacher_other` | `teacher.other@example.test` | `TEACHER` |
| `student_enrolled` | `student.enrolled@example.test` | `STUDENT` |
| `student_not_enrolled` | `student.unenrolled@example.test` | `STUDENT` |

Passwords are stored as BCrypt hashes compatible with the API.

The student references describe intended academic scenarios. This seed
does not create courses or enrollments.

## Prerequisites

Apply the official migrations first.

The API identity mapping requires:

- Table: `users`.
- Columns: `id`, `name`, `email`, `password_hash`, `role`.
- A unique constraint on `email`.
- Database-generated identifiers.

Final migration integration must preserve this mapping or update the API
and seed together.

## Execution

Run through PostgreSQL `psql` with `ON_ERROR_STOP=1`.

For the isolated authentication test container, run from the DB repository:

```powershell
Get-Content -Raw seeds/dev/identity.sql |
    docker exec -i att-book-auth-check psql `
        -U attbook_test `
        -d attbook_auth_test `
        -v ON_ERROR_STOP=1
```

The container name and credentials above belong to the isolated local
authentication check. The complete deployment must supply its own
database connection configuration.

## Repeat execution

Existing matching emails are not inserted again.

The script verifies that existing fixture accounts retain the expected
role and the exact seed password hash. Incompatible accounts cause an
error and transaction rollback; they are not overwritten.

## Academic integration

Load identity fixtures before academic fixtures.

Resolve user IDs by email using the reference mapping above.
Do not assume fixed numeric identifiers.

## Verification

- Execute the script twice.
- Verify that exactly four fixture accounts exist.
- Verify that their IDs remain unchanged.
- Log in through the API with a teacher and a student.
- Verify that responses return the expected roles.

Official migration and full deployment validation remain separate from
the isolated authentication check.