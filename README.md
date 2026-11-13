# TalentHop Integration Platform

AI-powered security operations integration platform built with Spring Boot 3.4 and Java 21.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                          REST API (8095)                            │
│  /api/v1/sources  /api/v1/events  /api/v1/pipelines  /swagger-ui   │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────────┐
│                        Controller Layer                              │
│  Thin controllers → delegate to services, return DTOs               │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────────┐
│                         Service Layer                                │
│  Business logic, @Transactional, validation, logging                │
│                                                                     │
│  ┌─────────────────────┐  ┌────────────────────┐  ┌──────────────┐ │
│  │IntegrationSource    │  │SecurityEvent       │  │Pipeline      │ │
│  │Service              │  │Service             │  │Service       │ │
│  └─────────┬───────────┘  └────────┬───────────┘  └──────┬───────┘ │
└────────────┼───────────────────────┼─────────────────────┼─────────┘
             │                       │                     │
┌────────────▼───────────────────────▼─────────────────────▼─────────┐
│                       Repository Layer                               │
│  Spring Data JPA, derived queries, @Query (GROUP BY)                │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
┌─────────────▼──────┐  ┌─────▼──────┐  ┌──────▼────────────────────┐
│  PostgreSQL (prod) │  │H2 (dev)    │  │  Apache Kafka             │
│  / Oracle          │  │in-memory   │  │  topic: security-events   │
└────────────────────┘  └────────────┘  │  (afterCommit publish)    │
                                        └───────────────────────────┘
```

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **Kafka publish after commit** (`TransactionSynchronization.afterCommit`) | Prevents publishing events for rolled-back transactions. If DB commit fails, no Kafka message. If Kafka fails, DB is already committed (at-least-once semantics). |
| **`@Version` on all entities** | Optimistic locking prevents lost updates in concurrent scenarios. `OptimisticLockException` → 409 Conflict. |
| **`ResourceNotFoundException` → 404** | Proper REST semantics. Not-found is not a "bad request". |
| **`DataIntegrityViolationException` → 409** | Handles TOCTOU race on unique constraints (e.g., duplicate name). |
| **Pagination on all list endpoints** (`Pageable`, default 50) | Prevents OOM on large datasets. |
| **DTO separation** (Request/Response) | API contract decoupled from JPA entities. No lazy-proxy serialization issues. |
| **`@JsonIgnore` on lazy relations** | Prevents `LazyInitializationException` during JSON serialization. |
| **`GROUP BY` for stats** (single query) | 5x fewer DB round-trips vs. separate COUNT per severity. |
| **FK check before delete** | `IntegrationSource.delete` checks for associated events → 409 if exists. |
| **`create-drop` in dev** | H2 in-memory is throwaway. Avoids stale schema issues. |

## Features

- **Integration Source Management** - CRUD for security data sources (SIEM, Firewall, Cloud, Endpoint)
- **Security Event Processing** - Ingest, query, filter, mark as processed
- **Transformation Pipelines** - Define and manage data transformation pipelines
- **Kafka Integration** - Event-driven architecture, transactional consistency
- **REST API** - Clean, well-documented, paginated endpoints
- **Swagger UI** - Interactive API documentation at `/swagger-ui.html`
- **Docker** - Multi-stage Docker build + docker-compose (app + PostgreSQL + Kafka)
- **H2 / PostgreSQL** - H2 for dev, PostgreSQL for production
- **Validation** - Bean Validation with proper error handling (400, 404, 409)
- **Global Exception Handling** - Granular: 400, 404, 409, 500

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Database | H2 (dev) / PostgreSQL (prod) |
| ORM | Spring Data JPA / Hibernate |
| Messaging | Apache Kafka |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Build | Maven |
| Container | Docker (multi-stage) |
| CI/CD | GitHub Actions |
| Testing | JUnit 5, MockMvc, PIT (mutation) |

## Testing

| Metric | Value |
|--------|-------|
| Integration tests | 26 |
| Line coverage | 82% |
| Mutation kill rate (PIT) | 67% (50/75) |
| Test strength | 83% |

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker (optional, for full stack)

### Run with H2 (dev)
```bash
mvn spring-boot:run
```

API: `http://localhost:8095`  
Swagger: `http://localhost:8095/swagger-ui.html`  
H2 Console: `http://localhost:8095/h2-console`

### Run with Docker Compose (full stack)
```bash
docker compose up --build
```

### Run Tests
```bash
mvn test
```

### Mutation Testing (PIT)
```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

## API Endpoints

### Integration Sources
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/sources` | Create source |
| GET | `/api/v1/sources` | List sources (paginated) |
| GET | `/api/v1/sources/{id}` | Get by ID |
| GET | `/api/v1/sources/name/{name}` | Get by name |
| PUT | `/api/v1/sources/{id}` | Update |
| DELETE | `/api/v1/sources/{id}` | Delete (409 if events exist) |

### Security Events
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/events` | Ingest event |
| GET | `/api/v1/events` | List (paginated, filterable) |
| GET | `/api/v1/events/source/{id}` | By source (`?includeProcessed=true`) |
| GET | `/api/v1/events/severity/{sev}` | By severity (unprocessed) |
| GET | `/api/v1/events/unprocessed?since=...` | Unprocessed since timestamp |
| GET | `/api/v1/events/stats` | Counts by severity (GROUP BY) |
| PATCH | `/api/v1/events/{id}/process` | Mark as processed (+ Kafka) |

### Pipelines
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/pipelines` | Create pipeline |
| GET | `/api/v1/pipelines` | List (paginated) |
| GET | `/api/v1/pipelines/{id}` | Get by ID |
| DELETE | `/api/v1/pipelines/{id}` | Delete |

## Project Structure

```
src/main/java/com/github/dziki/talenthop/
├── TalentHopIntegrationApplication.java
├── config/
│   ├── JpaConfig.java
│   └── OpenApiConfig.java
├── controller/
│   ├── IntegrationSourceController.java
│   ├── SecurityEventController.java
│   └── PipelineController.java
├── dto/
│   ├── IntegrationSourceRequest.java
│   ├── IntegrationSourceResponse.java
│   ├── SecurityEventRequest.java
│   ├── SecurityEventResponse.java
│   ├── PipelineRequest.java
│   └── PipelineResponse.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── kafka/
│   └── SecurityEventKafkaProducer.java
├── model/
│   ├── IntegrationSource.java
│   ├── IntegrationPipeline.java
│   └── SecurityEvent.java
├── repository/
│   ├── IntegrationSourceRepository.java
│   ├── IntegrationPipelineRepository.java
│   └── SecurityEventRepository.java
└── service/
    ├── IntegrationSourceService.java
    ├── SecurityEventService.java
    └── PipelineService.java
```

## License

MIT
