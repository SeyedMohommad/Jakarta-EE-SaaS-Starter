# Jakarta EE SaaS Starter (Multi‑Tenant + JWT)

A minimal yet production‑grade starter for building multi‑tenant SaaS with **Jakarta EE 10** and **MicroProfile 6**.
No vendor lock‑in — the same WAR runs on **WildFly**, **Open Liberty**, or **Payara** (Dockerfiles included).

## Features (v1)
- **Auth**: Register/Login with **JWT** and roles (`USER`/`ADMIN`) via MicroProfile JWT.
- **Multi‑Tenancy**: `tenant_id` column on every table + resolver from `X-Tenant-ID` header or subdomain of `Host`.
- **Domain**: `Project` / `Task` / `Comment` CRUD with pagination/sorting.
- **Realtime**: WebSocket endpoint `/ws/comments/{projectId}` broadcasts newly created comments.
- **Observability**: OpenAPI, Health/Readiness, and Metrics via MicroProfile.
- **Migrations**: PostgreSQL + Flyway with seed data (demo tenant: `demo`).

## Tech Stack
- **Jakarta EE**: JAX‑RS, CDI, JPA, Bean Validation, JSON‑B, WebSocket, Jakarta Security
- **MicroProfile**: Config, Health, Metrics, OpenAPI
- **DB**: PostgreSQL, **Flyway** migrations
- **Build**: Maven
- **Tests**: JUnit 5, RestAssured (placeholder), **Testcontainers** for DB
- **CI/CD**: GitHub Actions (build, test, Docker image)
- **Containers**: Docker + Docker Compose (app + db + optional Mailhog)

## Quickstart (Docker Compose, Payara Micro by default)
```bash
# Build the WAR
mvn -DskipTests package

# Start app + PostgreSQL
docker compose up --build
```
Then call the API using the tenant header:
- **Header**: `X-Tenant-ID: demo`
- **Login (seed user)**:
  ```http
  POST http://localhost:8080/api/auth/login
  Header: X-Tenant-ID: demo
  Body: {"email":"user@demo.local","password":"demo"}
  ```
- Use the returned JWT for subsequent requests: `Authorization: Bearer <token>`.

### Sample Endpoints
- `GET /api/projects` — supports `?page=&size=&sort=&asc=`
- `POST /api/projects` — `{ "name": "...", "description": "..." }`
- `GET /api/tasks`, `POST /api/tasks`
- `GET /api/comments`, `POST /api/comments` — creates a comment and broadcasts it via WebSocket

### WebSocket
Connect to:
```
ws://localhost:8080/ws/comments/{projectId}
```
Use the same tenant (`X-Tenant-ID`) when initiating the WebSocket connection (depends on server/runtime how headers are conveyed).
Newly created comments for the given tenant+project are pushed to connected clients.

## OpenAPI / Swagger UI
OpenAPI is enabled via MicroProfile. The exact UI path depends on the runtime (commonly `/openapi` or `/openapi/ui`).

## Multi‑Tenancy Details
- Tenancy is enforced by a `tenant_id` column on **all** entities.
- A request filter resolves the tenant from `X-Tenant-ID` (preferred) or the subdomain of the `Host` header.
- Repository methods scope queries and mutations to the current tenant ID.

## Database & Migrations
- JNDI DataSource name: **`jdbc/PostgresDS`**.
- Flyway runs on startup and applies migrations from `classpath:db/migration`.
- Seed data creates tenant `demo`, a sample project/task, and a demo user `user@demo.local` / `demo`.

## Runtimes
- **Payara Micro**: `Dockerfile.payara` (used by `docker-compose.yml`).
- **WildFly**: `Dockerfile.wildfly` (configure a datasource pointing to `jdbc/PostgresDS`; the compose file shows the basics).
- **Open Liberty**: `Dockerfile.openliberty` + a `server.xml` that defines the `jdbc/PostgresDS` dataSource and required features.

## Testing
- `mvn test` runs JUnit 5 tests.
- `FlywayMigrationTest` uses **Testcontainers** for PostgreSQL. Ensure Docker is available on the machine running tests.
- API tests can be extended with RestAssured when the server is up.

## Security Notes
- JWTs are issued with SmallRye JWT builder and validated via MicroProfile JWT.
- Demo RSA keys are shipped under `META-INF/keys` **for development only**. Replace with your own keys in production.
- Consider adding stronger authorization checks, input validation, and rate limiting for real deployments.

## Project Structure (high‑level)
```
src/main/java/dev/saasstarter/app
  ├─ resources/         (REST resources: auth, projects, tasks, comments)
  ├─ model/             (JPA entities: BaseEntity, UserAccount, Project, Task, Comment)
  ├─ repo/              (EntityManager producer + tenant‑aware repository)
  ├─ tenant/            (tenant context + resolver filter)
  ├─ websocket/         (comment broadcast WebSocket)
  └─ infra/             (Flyway migrator, health checks)
src/main/resources
  ├─ META-INF/persistence.xml
  ├─ META-INF/microprofile-config.properties
  ├─ META-INF/keys/ (demo public/private keys)
  └─ db/migration/ (Flyway SQL)
docker-compose.yml
Dockerfile.payara | Dockerfile.wildfly | Dockerfile.openliberty
.github/workflows/ci.yml
```

## How to Change or Extend
- Add filters and indexes for your access patterns.
- Introduce query‑level filters (e.g., Hibernate filters) if you need stricter tenant enforcement at the ORM level.
- Add Mailhog or any SMTP provider for email features.
- Expand the domain (labels, attachments, etc.) as needed.

---

**Happy shipping!**
