# 10 — Local Setup

## Purpose

This guide consolidates the local deployment instructions already documented
in the infrastructure repository.

It replaces the generic framework setup and its unrelated products endpoint.
It does not introduce functional requirements or claim a successful execution.

## Prerequisites

| Requirement | Purpose |
|---|---|
| Git | Obtain the project repositories. |
| Docker Desktop with Docker Compose | Build and run the database, API, and portal. |
| Network access during the first build | Download container images and application dependencies. |

Docker Desktop must be running before starting the services.

## Repository layout

The infrastructure configuration expects these sibling directories:

```text
project/
    att-book-api/
    att-book-db/
    att-book-infra/
    att-book-portal/
```

When using downloaded ZIP files, extract them and remove branch suffixes
such as `-develop` from the directory names.

Use compatible revisions of the repositories.
Do not mix unrelated development and release snapshots.

## Environment variables

Inside `att-book-infra`, create a local `.env` file:

```dotenv
DB_PASSWORD=<local-database-password>
JWT_SECRET=<base64-encoded-random-secret>
```

Replace the placeholders before starting the services.

The JWT secret must be valid Base64 and decode to at least 32 random bytes,
as required by the backend configuration.

Do not commit `.env`, real passwords, or JWT secrets.

## Start the application

Open a terminal inside `att-book-infra` and execute:

```powershell
docker compose -f compose.fullstack.yaml up -d --build
```

The configuration defines:

- PostgreSQL.
- The Spring Boot API.
- The React portal served by Nginx.

## Database initialization

The Compose configuration mounts SQL scripts from
`att-book-db/migrations` into the PostgreSQL initialization directory.

These scripts run automatically only when PostgreSQL initializes an empty
data directory.

They are not automatically reapplied to an existing database volume.
Database changes on an existing installation require a reviewed migration
procedure.

The API uses schema validation rather than automatic table creation.

## Check service status

```powershell
docker compose -f compose.fullstack.yaml ps
```

Check the status of `db`, `api`, and `portal`.
PostgreSQL should report a healthy state.

A running container alone does not prove that the complete application
workflow is working.

## Open the portal

Open:

```text
http://localhost:8086
```

The fullstack configuration publishes the portal on the local machine.
Nginx forwards `/api/v1` requests to the API over the Docker network.

The API and database ports are not published by this configuration.

## Optional demonstration data

The infrastructure README documents the following PowerShell commands.

Run them from `att-book-infra`, in this order:

```powershell
Get-Content ..\att-book-db\seeds\dev\identity.sql -Raw | docker compose -f compose.fullstack.yaml exec -T db psql -v ON_ERROR_STOP=1 -U attbook -d attbook

Get-Content ..\att-book-db\seeds\dev\academic.sql -Raw | docker compose -f compose.fullstack.yaml exec -T db psql -v ON_ERROR_STOP=1 -U attbook -d attbook

Get-Content ..\att-book-db\seeds\dev\sessions.sql -Raw | docker compose -f compose.fullstack.yaml exec -T db psql -v ON_ERROR_STOP=1 -U attbook -d attbook
```

These scripts prepare development users, courses, enrollments, and sessions.
They are not loaded automatically by the startup command.

Consult the seed documentation in `att-book-db` for demonstration account
details. Development credentials must not be reused outside that environment.

## Inspect logs

```powershell
docker compose -f compose.fullstack.yaml logs --tail=60 api
```

For database or portal problems:

```powershell
docker compose -f compose.fullstack.yaml logs --tail=60 db
docker compose -f compose.fullstack.yaml logs --tail=60 portal
```

Do not publish secrets, tokens, or personal data when sharing diagnostic output.

## Stop the application

```powershell
docker compose -f compose.fullstack.yaml down
```

This stops the services while preserving the database volume.

Do not add `-v` unless deletion of the stored database is intentional
and its consequences have been reviewed.

## Verification status

These commands are based on the supplied infrastructure documentation
and configuration.

Successful startup, authentication, persistence, and attendance workflows
must be supported by execution evidence.

NFR-08 concerns backend test coverage.
It does not define a one-hour installation requirement.

## Related documents

- [Infrastructure instructions](https://github.com/code-corhuila/att-book-infra/blob/develop/README.md)
- [Database repository](https://github.com/code-corhuila/att-book-db)
- [Migrations](../06-data/migrations.md)
- [Environments](./environments.md)
- [Testing strategy](../11-quality/testing-strategy.md)
