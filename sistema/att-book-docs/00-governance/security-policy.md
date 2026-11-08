# 00 — Security Policy

Attendance handles authenticated users, role-based functionality, QR-based
attendance registration, and persisted attendance information.

Security controls must protect credentials, authentication tokens, application
configuration, and attendance data.

## Security owner

**Owner:** Attendance development team

The Attendance development team is responsible for reviewing security-related
changes and addressing identified security issues.

## Secrets management

| Rule | Attendance policy |
|---|---|
| Secrets in source control | Passwords, tokens, JWT secrets, and database credentials must not be committed |
| Environment configuration | Environment-specific values are provided through environment variables |
| Example configuration | Example environment files contain variable names and non-sensitive example values only |
| Private configuration | Real local credentials and secrets remain outside version control |
| Documentation | Markdown files, examples, and screenshots must not expose real sensitive values |

Sensitive information includes:

- database passwords;
- authentication tokens;
- JWT secrets;
- private environment values;
- personal development credentials.

If a secret is exposed, it must no longer be considered safe and must be
revoked or replaced.

## Authentication

Attendance requires authentication for protected functionality.

Authentication tokens must be validated by the backend before protected
operations are executed.

The web portal must not be treated as proof that a request is
authenticated.

Passwords and authentication tokens must not be exposed in documentation or
application logs.

## Authorization

Attendance distinguishes between student and teacher functionality.

Authorization must be enforced by the backend and must not depend only on which
screens or buttons are visible in the web portal.

### Student access

Student functionality includes:

- registering attendance through a valid QR flow;
- reviewing the student's own attendance information.

### Teacher access

Teacher functionality includes:

- managing course-related information;
- managing class sessions;
- generating QR codes;
- reviewing attendance related to the corresponding classes.

A user must not gain unauthorized functionality only by modifying a request
sent by the client.

## QR attendance security

QR validation must be performed by the backend as part of the attendance flow.

Attendance registration must reject QR codes that are invalid or expired.

The system must prevent duplicate attendance registration for the same student
and class session.

QR validation must not rely exclusively on values trusted from the web
client.

## Database security

Attendance uses PostgreSQL for persistence.

Database credentials must be supplied through environment configuration rather
than being hard-coded in source files or documentation.

Application endpoints should expose only the information required by the
requested functionality.

## Logging

Application logs must not intentionally expose:

- passwords;
- authentication tokens;
- JWT secrets;
- database passwords;
- private environment variables.

Errors may include enough context for debugging without exposing the sensitive
value itself.

## Dependencies

Project dependencies should be reviewed before they are intentionally added or
upgraded.

Known security issues that affect Attendance should be addressed when they are
identified.

This policy does not assume the existence of an automated vulnerability scanner
unless one is actually configured in the project.

## Security incident response

When a credential, token, or secret is suspected to have been exposed:

1. stop using the exposed value when possible;
2. replace or rotate the affected value;
3. remove the sensitive value from active configuration;
4. review the affected files or environment;
5. document corrective changes that affect the project.

---

**Related:** [`documentation-rules.md`](./documentation-rules.md) · [`README.md`](./README.md)
