# API Container Runtime

## Scope

`compose.api.yaml` builds and runs the Attendance Registry API.

It requires an existing PostgreSQL database, an external Docker network
and an initialized schema matching the API entities.

It does not create tables, execute migrations or load demonstration seeds.

## Repository layout

Clone the repositories as siblings:

```text
GitHub/
  att-book-api/
  att-book-portal/
  att-book-infra/
  att-book-db/
```

The build uses the current working tree of `att-book-api`.
Before building, verify that it includes the authentication implementation.

## Image

- Build stage: Eclipse Temurin Java 17 JDK.
- Runtime stage: Eclipse Temurin Java 17 JRE.
- Maven Wrapper controls the Maven version.
- The runtime process uses a non-root user.
- Credentials are supplied at runtime, not during image construction.
- The build packages the application with tests skipped.

Execute the API tests separately before building the image.

## Runtime configuration

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL reachable from the API container |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | Base64 signing key containing at least 32 decoded bytes |
| `API_NETWORK` | Existing Docker network; defaults to `att-book-backend` |

The container listens on port 8080.
Compose publishes it at `127.0.0.1:8080`.

The API context path is `/api/v1`.

Do not use `localhost` in `DB_URL` to refer to a different container.
Use the database service name or its network alias.

## Isolated authentication check

The isolated database container is `att-book-auth-check`.
Its database is `attbook_auth_test`, with user `attbook_test`.

Create the network once and attach the existing database container:

```powershell
docker network create att-book-backend
docker network connect att-book-backend att-book-auth-check
```

Use the same PowerShell session that holds the database password and JWT
key from the local API check. Stop the host API before publishing port 8080.

Set the JDBC URL:

```powershell
$env:DB_URL = "jdbc:postgresql://att-book-auth-check:5432/attbook_auth_test"
```

From the infrastructure repository:

```powershell
docker compose -p att-book-runtime -f compose.api.yaml config --quiet
docker compose -p att-book-runtime -f compose.api.yaml up -d --build
docker compose -p att-book-runtime -f compose.api.yaml logs --tail 80 api
```

The temporary database schema used for this check is not a substitute for
the team's official migrations.

## Portal compatibility

The existing portal Nginx configuration proxies `/api/v1` to
`host.docker.internal:8080`.

The API container publishes that port on the host, preserving the existing
portal proxy configuration for Docker Desktop.

## Verification

- Confirm successful application startup and schema validation.
- Log in with a seeded teacher account.
- Retrieve `/api/v1/usuarios/me` using the returned bearer token.
- Verify a protected endpoint returns 401 without a token.
- Verify teacher course access succeeds.
- Verify portal login through its Nginx proxy when both services run.

Record these results after performing the checks.

## Shutdown

To stop and remove the API container:

```powershell
docker compose -p att-book-runtime -f compose.api.yaml down
```

This command does not remove the external database container or its data.

## Pending integration

- Official database migrations and seed execution.
- Full-service orchestration.
- Automated deployment checks.

## Verified execution — 2026-11-07

- Built and started the API container using Java 17.
- Connected to the isolated PostgreSQL database.
- Started successfully with Hibernate schema validation.
- Verified teacher login and authenticated profile retrieval.
- Built and started the portal container with a healthy status.
- Verified browser login through the Nginx proxy on port 8081.
- Created a course and confirmed it remained visible after refresh.
- Verified session restoration and logout.

This check used the isolated authentication database and temporary schema.
Official migrations, automated seed loading and complete project
orchestration remain pending integration.