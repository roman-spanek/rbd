# rbd

A simple Maven Java application that uses **Flyway** to manage versioned database migrations against a **PostgreSQL** database. No Spring Boot or application framework required.

## Project structure

```
.
├── pom.xml
└── src
    ├── main
    │   ├── java/com/example/flyway
    │   │   └── App.java                        # Application entry point
    │   └── resources/db/migration
    │       ├── V1__Create_users_table.sql       # Migration 1 – users table
    │       ├── V2__Add_email_to_users.sql       # Migration 2 – email column
    │       └── V3__Create_products_table.sql    # Migration 3 – products table
    └── test
        └── java/com/example/flyway
            └── AppTest.java                    # Unit tests (no DB required)
```

## Prerequisites

| Tool       | Version   |
|------------|-----------|
| Java       | 11 +      |
| Maven      | 3.8 +     |
| PostgreSQL | 12 +      |

## Configuration

The application reads connection details from **environment variables** or **system properties** (system properties take priority):

| Variable / Property | Default                                    | Description              |
|---------------------|--------------------------------------------|--------------------------|
| `DB_URL`            | `jdbc:postgresql://localhost:5432/mydb`    | JDBC connection URL      |
| `DB_USER`           | `postgres`                                 | Database username        |
| `DB_PASSWORD`       | _(empty)_                                  | Database password        |

## Build

```bash
mvn package
```

This produces a self-contained fat-jar at `target/flyway-postgres-demo-1.0.0-SNAPSHOT.jar`.

## Run migrations

### Option A – run the fat-jar directly

```bash
export DB_URL="jdbc:postgresql://localhost:5432/mydb"
export DB_USER="postgres"
export DB_PASSWORD="secret"

java -jar target/flyway-postgres-demo-1.0.0-SNAPSHOT.jar
```

### Option B – via the Flyway Maven plugin

```bash
mvn flyway:migrate \
  -Dflyway.url=jdbc:postgresql://localhost:5432/mydb \
  -Dflyway.user=postgres \
  -Dflyway.password=secret
```

### Option C – check current migration status

```bash
mvn flyway:info \
  -Dflyway.url=jdbc:postgresql://localhost:5432/mydb \
  -Dflyway.user=postgres \
  -Dflyway.password=secret
```

## Run tests

Unit tests do not require a running database:

```bash
mvn test
```

## Migrations

| Version | Description            |
|---------|------------------------|
| V1      | Create `users` table   |
| V2      | Add `email` column     |
| V3      | Create `products` table|

New migrations can be added by placing SQL files following the naming convention `V{n}__{Description}.sql` inside `src/main/resources/db/migration/`.
