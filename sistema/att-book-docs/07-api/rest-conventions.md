# 07 — REST Conventions

## Purpose

Attendance Registry exposes one HTTP API consumed by the Fullstack web
portal.

These conventions distinguish verified behavior from proposed operations.
A documented operation is not evidence that its implementation has been
merged or deployed.

## Base path and versioning

The API base path is:

```text
/api/v1
```

Spring Boot configures this prefix through:

```properties
server.servlet.context-path=/api/v1
```

OpenAPI contracts declare:

```yaml
servers:
  - url: /api/v1
```

Paths inside each contract remain relative to that base:

```yaml
paths:
  /auth/login:
```

The resulting URL is `/api/v1/auth/login`.

All modules share the same API version. Breaking changes require an
explicit migration strategy.

## Internal modules

Logical module boundaries are:

- `identity`
- `academic`
- `class-sessions`
- `attendance`

Resource names such as `usuarios` and `cursos` are HTTP resource names,
not separate API versions or necessarily physical database schemas.

## Resource naming

Use plural resource names and HTTP methods to express operations.

| Resource | Base path |
|---|---|
| Authentication | `/api/v1/auth` |
| Users | `/api/v1/usuarios` |
| Courses | `/api/v1/cursos` |
| Class sessions | `/api/v1/clases` |
| QR operations | `/api/v1/qr` |
| Attendance records | `/api/v1/asistencias` |

Authentication and QR validation retain their existing operation paths.

Nested resources may express ownership or association, for example:

```text
/api/v1/cursos/{cursoId}/clases
```

## HTTP methods

| Method | Usage | Example |
|---|---|---|
| GET | Retrieve information | `GET /api/v1/cursos` |
| POST | Create a resource or perform an operation | `POST /api/v1/auth/login` |
| PUT | Update an existing resource | `PUT /api/v1/cursos/{id}` |
| DELETE | Remove a resource | `DELETE /api/v1/cursos/{id}` |

## Verified identity operations

The following operations were checked with HTTP security tests and
manual requests against an isolated PostgreSQL database:

| Method | Relative path | Access |
|---|---|---|
| POST | `/auth/register` | Public; student registration only |
| POST | `/auth/login` | Public |
| GET | `/usuarios/me` | Authenticated student or teacher |

Registration returns public user information with status 201.
Login returns `token` and `user`.

User responses contain `id`, `nombre`, `email` and `rol`.
They never contain passwords or password hashes.

Registration accepts an optional role. Omitted or null roles default to
STUDENT. Requesting TEACHER through public registration returns 403.

Teacher demonstration accounts are provisioned through the controlled
identity seed in `att-book-db`.

## Verified course contract

| Method | Relative path | Success |
|---|---|---|
| GET | `/cursos` | 200; array of owned courses |
| GET | `/cursos/{id}` | 200 |
| POST | `/cursos` | 201; course and Location header |
| PUT | `/cursos/{id}` | 200 |
| DELETE | `/cursos/{id}` | 204; no response body |

All course operations require TEACHER.

The authenticated identity supplies the owner ID. Clients submit
`nombre` and `codigo`; they do not assign `profesorId`.

A teacher requesting another teacher's course receives 404.

The service and controller tests cover the CRUD and ownership rules.
Creation and listing were additionally verified manually with PostgreSQL
and through the web portal.

Course code uniqueness is not guaranteed by this contract.
Database integrity conflicts return 409.

## Operations requiring implementation verification

These paths are documented or proposed but were not verified in the
identity and course review:

| Method | Relative path | Status |
|---|---|---|
| GET | `/cursos/{cursoId}/clases` | Existing contract; implementation not verified |
| POST | `/cursos/{cursoId}/clases` | Existing contract; implementation not verified |
| GET | `/clases/{id}` | Existing contract; implementation not verified |
| POST | `/clases/{claseId}/qr` | Existing contract; implementation not verified |
| POST | `/qr/validate` | Existing contract; implementation not verified |
| POST | `/asistencias` | Existing contract; implementation not verified |
| GET | `/asistencias/historial` | Existing contract; implementation not verified |
| GET | `/clases/{claseId}/asistencias` | Proposed HU-11 path; confirm with attendance owner |

Review these contracts against the corresponding controllers and tests
before marking them as implemented.

Additional CRUD operations must follow the team's agreed MVP scope.
This document does not establish five complete entity CRUDs.

## Authentication and authorization

Protected requests send:

```http
Authorization: Bearer <token>
```

The backend validates the signature, issuer, timestamps and required
identity claims.

The reviewed authentication implementation uses:

- HS256 signatures.
- A Base64-configured key containing at least 32 decoded bytes.
- Issuer `att-book-api`.
- Default token lifetime of 3600 seconds.
- Stateless request authentication.
- Roles STUDENT and TEACHER.

The portal checks the authenticated profile when restoring a stored
session and removes its local token on logout or expiration.

Client-side role checks support navigation. Backend checks enforce access.

Browser logout does not revoke an already issued token. Such a token
remains subject to its expiration and server validation.

## HTTP status codes

| Code | Meaning |
|---|---|
| 200 | Successful request with response data |
| 201 | Resource created |
| 204 | Operation completed without a response body |
| 400 | Invalid request or field validation failure |
| 401 | Missing, invalid or expired credentials |
| 403 | Access policy rejects the operation |
| 404 | Resource unavailable or hidden by ownership policy |
| 409 | Conflict with current data or database constraints |
| 500 | Unexpected server failure |

Public registration requesting TEACHER returns 403 even though the
endpoint itself does not require authentication.

## Error responses

A single error body format has not yet been verified across the API.

The course exception handler uses Spring ProblemDetail responses for
the cases it handles. Authentication filters may return a status and
headers without the same JSON structure.

Clients must inspect the HTTP status before assuming an error body
schema.

The former `error`, `message`, `details` example is not a universally
implemented response format.

A unified error contract remains pending integration.

## Validation

Validate request DTOs before executing business operations.

Current identity and course validation includes:

- Required nonblank fields.
- Email format and maximum length.
- Course name and code length.
- Positive course identifiers.
- Registration passwords with at least eight characters.
- Passwords limited to 72 UTF-8 bytes for BCrypt compatibility.
- Unique normalized user emails.

Business validation for QR, enrollments and attendance must be verified
against the corresponding implementation.

## Pagination

The current course list returns an unpaginated array.

The `page` and `size` query parameters are not implemented for that
endpoint. Introduce pagination explicitly in the contract,
implementation, client and tests when agreed by the team.

## Verification evidence and integration limits

The identity and course work was checked with:

- 47 selected API tests.
- 16 portal tests.
- Portal production build and lint checks.
- Manual authentication and course creation/listing checks.
- API and portal containers connected to isolated PostgreSQL.

These results apply to the reviewed implementation and fixtures.
They do not certify official migrations, other modules, merged branches
or the complete release.

## Related documents

- [OpenAPI contracts](./contracts/openapi/)
- [Module boundaries](../02-domain/module-boundaries.md)
- [User story index](../04-requirements/user-stories.md)
- [Traceability matrix](../04-requirements/traceability-matrix.md)