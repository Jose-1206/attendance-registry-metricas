# att-book-infra

> Attendance Registry - Infrastructure: Compose/IaC, environments and secrets

Part of the **Attendance Registry** full-stack project — team `attendance-registry`, Complementaria III - Desarrollo Fullstack 2026-B.
Governance and documentation live in [`att-book-docs`](https://github.com/code-corhuila/att-book-docs).

## Branching

Three permanent branches. **None of them accepts a direct commit** — you enter through a child
branch and leave through a Pull Request.

```
develop  <--PR--  feat/... fix/... chore/...
qa       <--PR--  develop
main     <--PR--  qa (release)   hotfix/...
```

`develop` is the default branch: start every task from it. `main` requires **1 approval from
`ariel5253`**. On `develop` and `qa` the team sets its own review rule.

Commit and branch naming: `00-governance/git-conventions.md` in `att-book-docs`.

## Full Stack - Ejecución local con Docker Compose

Este repositorio permite ejecutar los tres servicios principales de Attendance Registry:

- **PostgreSQL 17:** base de datos.
- **Spring Boot:** API REST.
- **React + Nginx:** portal web.

### Requisitos

- Docker Desktop instalado y ejecutándose.
- Git.
- Los repositorios `att-book-infra`, `att-book-api`, `att-book-portal` y `att-book-db` descargados en una misma carpeta.

### Estructura de carpetas

```text
proyecto/
├── att-book-api/
├── att-book-db/
├── att-book-infra/
└── att-book-portal/
```

### 1. Configurar variables de entorno

Dentro de `att-book-infra`, crear un archivo `.env` con las siguientes variables:

```dotenv
DB_PASSWORD=<contraseña-local-segura>
JWT_SECRET=<secreto-local-seguro>
```

Cada integrante debe generar sus propios valores.

**No subir `.env` a GitHub.** Este archivo está excluido mediante `.gitignore`.

### 2. Iniciar los servicios

Desde una terminal ubicada en `att-book-infra`:

```powershell
docker compose -f compose.fullstack.yaml up -d --build
```

La primera ejecución crea el volumen de PostgreSQL e inicializa las tablas mediante los archivos SQL de `att-book-db/migrations`.

Las migraciones de esta configuración se ejecutan automáticamente solo al crear una base de datos nueva. No se aplican automáticamente a volúmenes existentes.

### 3. Verificar los servicios

```powershell
docker compose -f compose.fullstack.yaml ps
```

Comprobar que los servicios `db`, `api` y `portal` estén ejecutándose y que PostgreSQL aparezca como saludable (`healthy`).

### 4. Abrir la aplicación

Acceder desde el navegador a:

http://localhost:8086

### 5. Cargar datos de demostración (opcional)

Desde PowerShell, dentro de `att-book-infra`, ejecutar en este orden:

```powershell
Get-Content ..\att-book-db\seeds\dev\identity.sql -Raw | docker compose -f compose.fullstack.yaml exec -T db psql -v ON_ERROR_STOP=1 -U attbook -d attbook

Get-Content ..\att-book-db\seeds\dev\academic.sql -Raw | docker compose -f compose.fullstack.yaml exec -T db psql -v ON_ERROR_STOP=1 -U attbook -d attbook

Get-Content ..\att-book-db\seeds\dev\sessions.sql -Raw | docker compose -f compose.fullstack.yaml exec -T db psql -v ON_ERROR_STOP=1 -U attbook -d attbook
```

Los scripts permiten preparar usuarios, cursos, inscripciones y sesiones para pruebas locales.

### 6. Consultar los registros del backend

```powershell
docker compose -f compose.fullstack.yaml logs --tail=60 api
```

### 7. Detener el proyecto

```powershell
docker compose -f compose.fullstack.yaml down
```

Este comando detiene y elimina los contenedores y la red del proyecto, pero conserva el volumen de PostgreSQL.

**Precaución:** evitar `docker compose down -v` si se desea conservar la información de la base de datos.

### Notas

- La API se comunica con PostgreSQL mediante la red interna de Docker.
- El portal está disponible localmente en el puerto `8086`.
- Los datos de demostración no se cargan automáticamente; deben ejecutarse los scripts indicados.
- Esta configuración está orientada al desarrollo local.
