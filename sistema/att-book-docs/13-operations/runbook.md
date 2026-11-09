# 13 — Runbook

## Purpose

This document routes operational symptoms to the relevant Attendance Registry
area and references the existing deployment instructions.

It replaces unrelated product and invoice examples from the framework.

The procedures require execution evidence before they can be considered
validated recovery instructions.

## Operational boundaries

The backend modules are part of the same application.
They are not independently deployed services.

The local infrastructure configuration defines separate database, API,
and portal containers.

Investigating a module does not mean restarting it as a separate process.

## Symptom routing

| Symptom | Area to investigate | Initial action |
|---|---|---|
| Sign-in fails | `identity` | Check the API response, account configuration, and authentication logs without exposing credentials. |
| A course operation is rejected | `academic` | Check the user's role, course ownership, and documented business rules. |
| A session or QR operation fails | `class-sessions` | Check session ownership and the relevant API response. |
| Attendance registration is rejected | `attendance` | Check QR validity, course enrollment, and whether attendance already exists. |
| The portal is unavailable | Portal service | Review container status and portal logs. |
| The portal cannot reach the API | Portal and API services | Check API startup, proxy configuration, and the shared Docker network. |
| The API cannot access its database | API and database services | Review database readiness, connection configuration, and schema initialization. |
| Errors begin after a deployment | Changed revision and configuration | Compare the deployed revisions and configuration with the previous working state. |

An expected rejection, such as an expired QR or duplicate attendance,
is not by itself evidence of a service outage.

## Local service status

Run the following commands from `att-book-infra`:

```powershell
docker compose -f compose.fullstack.yaml ps
```

Inspect the relevant service logs:

```powershell
docker compose -f compose.fullstack.yaml logs --tail=60 api
docker compose -f compose.fullstack.yaml logs --tail=60 db
docker compose -f compose.fullstack.yaml logs --tail=60 portal
```

These commands come from the existing infrastructure configuration.
Their inclusion does not establish that an operational drill has passed.

Review diagnostic output for secrets, tokens, and personal data before
sharing it.

## Startup

Follow the prerequisites, repository layout, and environment configuration
in the local setup guide.

From `att-book-infra`:

```powershell
docker compose -f compose.fullstack.yaml up -d --build
```

Review service status and logs after startup.

A running container or a successful web-server response alone does not prove
that authentication, database access, and attendance registration work.

## Shutdown

From `att-book-infra`:

```powershell
docker compose -f compose.fullstack.yaml down
```

This stops the services while retaining the database volume.

Do not add `-v` during routine shutdown.
That option removes the Compose-managed database volume.

## Database initialization issues

The supplied configuration initializes SQL scripts when PostgreSQL starts
with an empty data directory.

Adding migration files does not automatically apply them to an existing
database volume.

Before changing an existing database:

1. Identify its current structure and applied changes.
2. Review the required migration and compatibility impact.
3. Preserve existing data through the appropriate backup procedure.
4. Apply the reviewed procedure and verify its result.

Do not delete the database volume merely to hide an initialization problem.

## Recovery and rollback

Before attempting recovery:

1. Identify the affected revision and configuration.
2. Preserve relevant diagnostic evidence.
3. Determine whether the problem involves application code, configuration,
   or database structure.
4. Review compatibility before restoring a previous application revision.
5. Verify the affected workflow after the corrective action.

Restoring an older application revision does not automatically reverse
database changes.

No automatic rollback pipeline or tested database restoration procedure
is claimed by this document.

## Verification record

An operational verification record should identify:

- Environment and repository revisions.
- Observed symptom.
- Diagnostic commands and relevant results.
- Corrective action.
- Verification outcome.

Do not mark a recovery procedure as validated without an actual execution.

## Requirement references

- NFR-03 defines the availability target.
- NFR-09 defines error traceability expectations.
- NFR-10 concerns usability of the student QR attendance flow.

NFR-10 does not define invoice support or incident resolution time.

## Related documents

- [Observability](./observability.md)
- [Local setup](../10-devops/local-setup.md)
- [Environments](../10-devops/environments.md)
- [Migrations](../06-data/migrations.md)
- [Module catalog](../09-modules/module-catalog.md)
