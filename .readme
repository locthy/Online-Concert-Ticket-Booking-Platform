# Flashsale Project (.readme)

## 1) Coding Guideline & Convention

### Architecture
**API Pattern: Controller -> Service -> Repository**

- **Controller**: Expose REST endpoints, validate input, map Request DTO -> Service call, and return Response DTO.
- **Service**: Hold business logic, orchestration, transactions, idempotency, integrations (Redis/Kafka), and domain rules.
- **Repository**: Data access layer (Spring Data JPA). No business logic here.

**DTOs (Data Transfer Objects)**

- Request DTOs: `dto/request/*` (input validation with `jakarta.validation` annotations).
- Response DTOs: `dto/response/*` (stable API contract).
- Payload/event DTOs: `dto/payload/*` (Kafka/async internal messages).

### How To Add A New Feature
1. Define endpoints in a new/existing `controller/*Controller`.
2. Create Request/Response DTOs in `dto/request` and `dto/response`.
3. Implement business logic in a new/existing `service/*Service`.
4. Add data access methods in `repository/*Repository` (Spring Data JPA query methods as needed).
5. Add/extend Entities in `entity/*` only when you truly need persistent data changes.
6. Add unit tests for service/controller behavior (see Testing below).

### Unit Testing
- Frameworks: **JUnit 5** + **Mockito** (via `spring-boot-starter-test`).
- Run all tests:
```bash
mvn test
```

### Naming
- Variables & methods: **camelCase**
- Classes & interfaces: **PascalCase**
- Constants: `UPPER_SNAKE_CASE` (when used)

## 2) Local Setup & Run

### Prerequisites
- **Java 17** (project currently uses `java.version=17` in `pom.xml`; Java 21 is also acceptable if your toolchain supports it)
- **Maven**
- **Docker** (Docker Desktop on Windows/macOS, Docker Engine on Linux)

### Infrastructure
Start required services (PostgreSQL, Kafka, Redis):
```bash
docker-compose up -d
```

Note: Ensure the credentials/ports in `docker-compose.yml` match `src/main/resources/application.yaml`.

### Application
Build:
```bash
mvn clean install
```

Run (option A, Maven):
```bash
mvn spring-boot:run
```

Run (option B, IDE): run the main class:
- `com.geekup.flashsale.FlashsaleApplication`

Default local port:
- `http://localhost:8080`

## 3) API Documentation (Swagger)

Swagger UI is enabled via `springdoc-openapi-starter-webmvc-ui` (added in `pom.xml`).

Access:
- `http://localhost:8080/swagger-ui/index.html`

## 4) API Testing Collections (Postman)

Files are stored in:
- `/docs/postman`

Included:
- `flashsale.postman_collection.json` (Checkout, Order Status, Inventory Check)
- `flashsale.local.postman_environment.json` (contains `base_url=http://localhost:8080`)

