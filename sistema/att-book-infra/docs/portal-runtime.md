# Portal Runtime

## Requirements

- Docker Desktop running.
- Sibling clones named `att-book-infra` and `att-book-portal`.
- The portal clone must contain the React application and package lockfile.

The image builds the current local contents of the portal clone.
Pending portal changes must be integrated before testing from clean clones.

## Start

Run from `att-book-infra`:

```bash
docker compose -f compose.portal.yaml config --quiet
docker compose -f compose.portal.yaml up -d --build
```

Open http://localhost:8081.

The portal is published on the local loopback interface only.

## Verify

```bash
docker compose -f compose.portal.yaml ps
docker compose -f compose.portal.yaml exec portal nginx -t
curl -i http://localhost:8081/health
```

Expected results:

- Portal container becomes healthy.
- Nginx configuration validation succeeds.
- `/health` returns HTTP 200 with body `ok`.

The health check verifies Nginx availability, not backend connectivity.

## API Routing

Requests under `/api/v1` are forwarded to:

    http://host.docker.internal:8080

The path is preserved. The API must be running on the host and reachable
from the container.

For the complete Compose deployment, replace this upstream with the API
service name and its internal port.

The Vite development proxy is not used by this container.
Nginx handles runtime proxying and SPA route fallback.

## Authentication

The current course page requests teacher login until the authentication
module supplies a valid access token.

Displaying that message confirms that the page loads; it does not verify
the complete authenticated course workflow.

## Rebuild After Portal Changes

```bash
docker compose -f compose.portal.yaml up -d --build
```

## Logs

```bash
docker compose -f compose.portal.yaml logs --tail=100 portal
```

## Stop

```bash
docker compose -f compose.portal.yaml down
```

## Remaining Integration

- Merge the portal implementation into its agreed integration branch.
- Integrate authentication and database migrations.
- Verify authenticated course operations through Nginx.
- Integrate portal, API and database into the complete Compose deployment.
- Repeat verification from clean clones.