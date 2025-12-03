# Jakarta EE SaaS Starter (Multi-Tenant + JWT)

A minimal, production-ready starter for building multi-tenant SaaS applications using **Jakarta EE 10** and **MicroProfile 6** — without any vendor lock-in. The same WAR is deployable on **WildFly**, **Open Liberty**, or **Payara Micro**.

## Features

* **Authentication**: User registration & login with **JWT** and role-based access (`USER`, `ADMIN`).
* **Multi-Tenancy**: Automatic tenant scoping using a `tenant_id` column and resolution from the `X-Tenant-ID` header or subdomain.
* **Domain Model**: CRUD for **Projects**, **Tasks**, and **Comments** with pagination and sorting.
* **Real-time Updates**: WebSocket endpoint `/ws/comments/{projectId}` broadcasting new comments instantly.
* **Observability**: OpenAPI, Health, Readiness, and Metrics via MicroProfile.
* **Embedded Database**: Uses **H2 in-memory database** for fast development with automatic schema generation.

## Technology Stack

* **Jakarta EE**: JAX-RS, CDI, JPA, JSON-B, WebSocket, Bean Validation, Jakarta Security
* **MicroProfile**: Config, Health, Metrics, OpenAPI
* **Database**: **H2 (in-memory)** — zero setup, auto-initializing schema
* **Build**: Maven
* **Testing**: JUnit 5, RestAssured (API tests placeholder)
* **Containers**: Docker + Docker Compose (application + optional mail catcher)

## Quickstart (Docker Compose + Payara Micro)

```bash
# Build the WAR
mvn -DskipTests package

# Start the application (H2 is embedded, no DB container required)
docker compose up --build
```

### Calling the API

All API requests must include a tenant header:

```
X-Tenant-ID: demo
```

#### Login (demo user)

```http
POST http://localhost:8080/api/auth/login
Header: X-Tenant-ID: demo
Body: {"email":"user@demo.local","password":"demo"}
```

Use the returned JWT for authenticated endpoints:

```
Authorization: Bearer <token>
```

### Example Endpoints

* `GET /api/projects`
* `POST /api/projects`
* `GET /api/tasks`
* `POST /api/tasks`
* `GET /api/comments`
* `POST /api/comments`

### WebSocket

```
ws://localhost:8080/ws/comments/{projectId}
```

Sends real-time comments for the same tenant.

## Multi-Tenancy Behavior

* Every entity includes a `tenant_id` column.
* A request filter resolves the active tenant.
* All repository queries are automatically scoped to the current tenant.

## Database (H2 In-Memory)

* No setup or installation required.
* Fast startup and auto-reset on each run.
* Schema is generated automatically via JPA (`drop-and-create`).
* Perfect for development, demos, and testing.

## Supported Runtimes

* **Payara Micro** — default Docker runtime
* **WildFly** — simple deployment to `standalone/deployments`
* **Open Liberty** — drop-ins support + configurable `server.xml`

## Testing

* Unit tests via **JUnit 5**
* Integration tests planned using RestAssured

## Security Notes

* JWTs are generated via SmallRye JWT
* Demo RSA keys included for local development — **replace in production**
* Role-based access control supported

## Project Structure

```
src/main/java/dev/saasstarter/app
  ├─ resources/      # REST API (auth, projects, tasks, comments)
  ├─ model/          # JPA entities
  ├─ repo/           # EntityManager producer + tenant-aware repository
  ├─ tenant/         # tenant context + resolver filter
  ├─ websocket/      # comment broadcast WebSocket
  └─ infra/          # config, health checks
src/main/resources
  ├─ META-INF/persistence.xml
  ├─ META-INF/microprofile-config.properties
  └─ META-INF/keys/ (development JWT keys)
docker-compose.yml
Dockerfile.payara | Dockerfile.wildfly | Dockerfile.openliberty
```

## How to Extend

* Add new entities or modules (labels, attachments, notifications, etc.)
* Add custom OpenAPI filters or documentation
* Replace H2 with a production-grade external database when needed

---

**Happy shipping! 🚀**
