# Authentication and Authorization

## Implemented endpoints

All paths use the `/api/v1` server context.

| Method | Path | Access |
|---|---|---|
| POST | `/auth/register` | Public; student registration only |
| POST | `/auth/login` | Public |
| GET | `/usuarios/me` | Authenticated user |

Registration accepts `nombre`, `email`, `password` and an optional `rol`.
The role must be `STUDENT` when supplied. Teacher registration through
the public endpoint is rejected.

Login returns `token` and `user`. User responses exclude passwords
and password hashes.

## Security

- Passwords are hashed with BCrypt.
- Emails are normalized before lookup and persistence.
- A database unique constraint protects email uniqueness.
- Bearer JWTs use HS256 signatures and a configured issuer.
- Tokens expire after 3600 seconds.
- Protected requests use stateless authentication.
- The JWT authentication object implements `CoursePrincipal`.
- Course management requires the `TEACHER` role.
- Course ownership checks remain in the course service.

Teacher accounts must be provisioned through the controlled database
seed process.

## Configuration

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Base64-encoded signing key containing at least 32 decoded bytes |
| `SERVER_PORT` | Optional HTTP port; defaults to 8080 |

Do not commit real credentials or signing keys.

Hibernate uses `ddl-auto=validate`. Database migrations must create the
required tables before application startup.

The identity entity maps to `users` with columns `id`, `name`, `email`,
`password_hash` and `role`. The schema must enforce unique emails.

## Automated validation

Run from the API repository root:

```powershell
.\mvnw.cmd "-Dtest=AttendanceAuthenticationTest,JwtServiceTest,IdentityServiceTest,IdentityHttpSecurityTest,CourseServiceTest,CourseControllerTest" test
```

Verified result: 47 tests, zero failures and zero errors.

These tests cover identity services, JWT validation, HTTP security and
the existing course service and controller.

## Manual PostgreSQL validation

Verified on 2026-11-07 using an isolated PostgreSQL database:

- Application startup with Hibernate schema validation.
- Student registration, login and authenticated profile retrieval.
- Profile requests without a token: 401.
- Profile requests with an invalid token: 401.
- Student access to course management: 403.
- Teacher login.
- Course creation and listing using the teacher token.
- The created course owner matches the authenticated teacher ID.

The teacher fixture was registered as a student and assigned the teacher
role directly in the isolated test database before login.

## Integration status

The manual check used a temporary schema containing `users` and `courses`.
It does not validate the team's final migrations or demonstration seeds.

Pending integration:

- Align and verify the official database migrations.
- Add controlled teacher seed accounts.
- Connect portal authentication.
- Verify the complete containerized deployment.