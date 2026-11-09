\# Local Deployment Verification



\## Purpose



This guide describes how to verify the local Docker deployment of the Attendance Registry API and portal.



It complements `api-runtime.md` and `portal-runtime.md` by providing a common verification checklist, troubleshooting procedures, and an evidence record.



This document does not replace the official database migrations or establish that the complete application stack has been integrated.



\## Prerequisites



Before starting verification, confirm that:



\- Docker Desktop is running.

\- Docker Compose is available.

\- The `att-book-api`, `att-book-portal`, and `att-book-infra` repositories are cloned as sibling directories.

\- The API has access to a PostgreSQL database with the required schema.

\- The external Docker network required by the API exists.

\- The required API environment variables are configured.

\- Ports `8080` and `8081` are available.



Required API environment variables:



| Variable | Purpose |

|---|---|

| `DB\_URL` | JDBC connection URL for PostgreSQL |

| `DB\_USERNAME` | Database username |

| `DB\_PASSWORD` | Database password |

| `JWT\_SECRET` | Base64-encoded JWT signing key |

| `API\_NETWORK` | Optional external Docker network name |



Never commit real passwords, JWT secrets, or environment files containing credentials.



\## 1. Verify Docker Availability



Run:



```powershell

docker version

docker compose version

docker info

```



Expected result:



\- Docker CLI is available.

\- Docker Compose is installed.

\- Docker Engine responds successfully.



If Docker Engine is unavailable, start Docker Desktop before continuing.



\## 2. Verify the External Network



List Docker networks:



```powershell

docker network ls

```



The API Compose configuration uses `att-book-backend` by default.



If the network does not exist, create it:



```powershell

docker network create att-book-backend

```



If a different network is configured through `API\_NETWORK`, verify that network instead.



The PostgreSQL container must also be connected to the network used by the API.



\## 3. Validate Compose Configuration



From the `att-book-infra` repository, run:



```powershell

docker compose -p att-book-runtime -f compose.api.yaml config --quiet

docker compose -f compose.portal.yaml config --quiet

```



Expected result:



\- Both commands complete without configuration errors.



The API command requires `DB\_URL`, `DB\_USERNAME`, `DB\_PASSWORD`, and `JWT\_SECRET` to be defined in the current environment.



Configuration validation does not confirm database connectivity or application functionality.



\## 4. Verify the API Container



Follow `docs/api-runtime.md` to configure the database and start the API.



Check container status:



```powershell

docker compose -p att-book-runtime -f compose.api.yaml ps

```



Inspect recent logs:



```powershell

docker compose -p att-book-runtime -f compose.api.yaml logs --tail 100 api

```



Expected result:



\- The API container remains running.

\- Application startup completes successfully.

\- No database authentication or schema validation errors appear.



The API is published on `127.0.0.1:8080` and uses the `/api/v1` context path.



Do not interpret a running container as proof that authentication or database operations work correctly.



\## 5. Verify the Portal Container



Follow `docs/portal-runtime.md` to start the portal.



Check its status:



```powershell

docker compose -f compose.portal.yaml ps

```



Validate Nginx:



```powershell

docker compose -f compose.portal.yaml exec portal nginx -t

```



Verify the health endpoint:



```powershell

curl.exe -i http://localhost:8081/health

```



Expected result:



\- The portal container reports a healthy status.

\- Nginx configuration validation succeeds.

\- The health endpoint returns HTTP 200 with the response body `ok`.



The portal health endpoint checks Nginx availability, not API connectivity.



\## 6. Verify Portal-to-API Connectivity



Open:



```text

http://localhost:8081

```



The current Nginx configuration forwards `/api/v1` requests to `host.docker.internal:8080`.



When a valid test account and initialized database are available:



1\. Open the portal login page.

2\. Authenticate with the test account.

3\. Verify that the application displays the expected authenticated view.

4\. Refresh the page and verify the expected session behavior.

5\. Log out and confirm that protected content is no longer accessible.



Record the results of these checks. Do not mark them as passed without executing them.



\## 7. Troubleshooting



| Problem | Possible cause | Verification or action |

|---|---|---|

| Docker commands fail | Docker Desktop is stopped | Check `docker info` |

| API configuration fails | Required environment variable is missing | Review variables in the current terminal |

| External network not found | Docker network has not been created | Check `docker network ls` |

| Database connection refused | Database is stopped or unreachable | Check database container status and network |

| Database authentication fails | Incorrect database credentials | Verify credentials without printing secrets |

| API exits during startup | Application or schema validation error | Inspect API container logs |

| Port 8080 is unavailable | Another process uses the API port | Stop the conflicting process |

| Port 8081 is unavailable | Another process uses the portal port | Stop the conflicting process |

| Portal is unhealthy | Nginx failed to start or health check failed | Inspect portal logs and validate Nginx |

| Portal loads but login fails | API, database, authentication, or proxy issue | Inspect API and portal logs |



\## 8. Verification Evidence



Use this table to record actual results.



| Check | Expected result | Actual result | Status |

|---|---|---|---|

| Docker Engine | Available | Not tested | Pending |

| External network | Exists | Not tested | Pending |

| API Compose configuration | Valid | Not tested | Pending |

| Portal Compose configuration | Valid | Not tested | Pending |

| API container | Running | Not tested | Pending |

| API startup logs | No startup errors | Not tested | Pending |

| Portal container | Healthy | Not tested | Pending |

| Nginx configuration | Valid | Not tested | Pending |

| Portal health endpoint | HTTP 200 | Not tested | Pending |

| Portal login | Successful with valid test account | Not tested | Pending |

| Session restoration | Expected behavior confirmed | Not tested | Pending |

| Logout | Protected content becomes inaccessible | Not tested | Pending |



When collecting evidence:



\- Record the verification date.

\- Identify the Git branch or commit being tested.

\- Save relevant command output or screenshots.

\- Do not expose passwords, JWT tokens, or other secrets.

\- Clearly distinguish successful checks from pending or failed checks.



\## 9. Shutdown



Stop the portal:



```powershell

docker compose -f compose.portal.yaml down

```



Stop the API:



```powershell

docker compose -p att-book-runtime -f compose.api.yaml down

```



These commands do not remove the external PostgreSQL database or its data.



\## 10. Known Limitations



At the time of writing, the existing infrastructure documentation identifies the following pending work:



\- Integration of official database migrations and seed execution.

\- Complete orchestration of the database, API, and portal.

\- Automated deployment verification.

\- Repeated verification from clean repository clones.



This guide provides a manual verification procedure. It does not claim that these pending integrations are complete.



\## Related Documentation



\- `docs/api-runtime.md`

\- `docs/portal-runtime.md`

\- `compose.api.yaml`

\- `compose.portal.yaml`