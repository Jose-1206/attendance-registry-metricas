# Local Development

## Requirements

- JDK 17
- Docker Desktop running
- Git

Maven is provided through the Maven Wrapper.

## Start PostgreSQL

Run this command once to create the development container:

```bash
docker run -d \
  --name att-book-postgres-dev \
  -e POSTGRES_DB=att_book \
  -e POSTGRES_USER=att_book \
  -e POSTGRES_PASSWORD=att_book_local \
  -p 127.0.0.1:5433:5432 \
  -v att-book-postgres-dev-data:/var/lib/postgresql/data \
  postgres:17
```

These credentials are intended only for local development.

For subsequent sessions, start the existing container:

```bash
docker start att-book-postgres-dev
```

Check database readiness:

```bash
docker exec att-book-postgres-dev pg_isready -U att_book -d att_book
```

Wait until the command reports `accepting connections`.

## Configure the Environment

Run these commands in the terminal used to execute Maven:

```bash
export DB_URL='jdbc:postgresql://localhost:5433/att_book'
export DB_USERNAME='att_book'
export DB_PASSWORD='att_book_local'
```

On macOS, select JDK 17 if necessary:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

## Build and Test

```bash
./mvnw clean verify
```

The generated context test requires PostgreSQL to be running.

## Run the Application

```bash
./mvnw spring-boot:run
```

The application uses port `8080` by default and the context path `/api/v1`.

At this bootstrap stage, no REST controllers are implemented. A request to
`http://localhost:8080/api/v1/` returns HTTP `404`.

## Database Schema

Hibernate uses `ddl-auto=validate`. It does not create or update tables.
Once entities are introduced, the corresponding schema must be provisioned
through the migration workflow agreed with `att-book-db`.

## Stop the Application

Press `Ctrl+C` in the terminal running Spring Boot.

To stop the development database:

```bash
docker stop att-book-postgres-dev
```

Database data remains in the named Docker volume.