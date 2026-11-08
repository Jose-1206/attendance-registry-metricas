# Course Module

## Implementation

The module provides teacher-owned course management.

| Method | Application URL | Success status |
|---|---|---|
| GET | `/api/v1/cursos` | 200 |
| GET | `/api/v1/cursos/{id}` | 200 |
| POST | `/api/v1/cursos` | 201 |
| PUT | `/api/v1/cursos/{id}` | 200 |
| DELETE | `/api/v1/cursos/{id}` | 204 |

Create and update requests use:

```json
{
  "nombre": "Fullstack",
  "codigo": "FS-01"
}
```

Responses contain `id`, `nombre`, `codigo` and `profesorId`.

The owner is obtained from the authenticated identity, never from a
request field.

## Identity Integration — Jose Miguel

The controller requires a verified Principal implementing CoursePrincipal,
or an equivalent adapter to the final authentication implementation.

The identity must provide:

- The registered user's database identifier.
- The verified role, using `TEACHER` or `STUDENT`.

Only teachers can access the management operations implemented here.
A teacher can manage only their own courses.

Without the identity integration, requests are rejected with HTTP 401.
JWT validation is not implemented by this module.

Student course visibility remains pending integration with enrollments.

## Database Integration — Luis Fernando

The current JPA mapping expects a `courses` table in the configured
default schema with these columns:

| Column | PostgreSQL type | Constraint |
|---|---|---|
| id | bigint | Identity primary key |
| name | varchar(150) | Not null |
| code | varchar(30) | Not null |
| teacher_id | bigint | Not null; references the agreed user table |

Confirm this mapping against the final database migrations before integration.

The database must enforce the teacher reference. The identity integration
must ensure that the owner is a registered teacher.

Enrollment and class-session foreign keys must restrict course deletion
when related records exist. Such conflicts are returned as HTTP 409.

Hibernate uses `ddl-auto=validate`; it does not create the table.

## Contract Alignment — Jose Miguel

Update the OpenAPI contract to:

- Use `/api/v1` as the server base.
- Include required `codigo` in create and update requests.
- Include `codigo` in course responses.
- Document PUT and DELETE operations.
- Document teacher ownership, HTTP 403 and integrity conflicts.
- Distinguish teacher management from future student course visibility.

## Verification

Run the isolated service and controller tests:

```bash
./mvnw -Dtest=CourseServiceTest,CourseControllerTest test
```

These tests cover service authorization, ownership filtering, controller
responses, request validation and integrity-error handling using mocks.

They do not verify JWT, actual foreign keys or PostgreSQL persistence.

Full application verification remains pending database provisioning
and identity integration:

```bash
./mvnw clean verify
```