# TalentHop Integration Platform

AI-powered security operations integration platform built with Spring Boot 3.4 and Java 21.

## Features

- **Integration Source Management** - CRUD for security data sources (SIEM, Firewall, Cloud, Endpoint)
- **Security Event Processing** - Ingest, query, and filter security events
- **Transformation Pipelines** - Define and manage data transformation pipelines
- **Kafka Integration** - Event-driven architecture with Apache Kafka
- **REST API** - Clean, well-documented REST endpoints
- **Swagger UI** - Interactive API documentation at `/swagger-ui.html`
- **Docker** - Multi-stage Docker build + docker-compose (app + PostgreSQL + Kafka)
- **GraalVM Native Image** - Ready for native compilation
- **H2 / PostgreSQL** - H2 for dev, PostgreSQL for production
- **Validation** - Bean Validation with proper error handling
- **Global Exception Handling** - Clean error responses

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
| Native | GraalVM Native Image (ready) |

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker (optional, for full stack)

### Run with H2 (dev)
```bash
mvn spring-boot:run
```

API available at: `http://localhost:8095`
Swagger UI: `http://localhost:8095/swagger-ui.html`
H2 Console: `http://localhost:8095/h2-console`

### Run with Docker Compose (full stack)
```bash
docker compose up --build
```

This starts:
- App on port 8095
- PostgreSQL on port 5432
- Kafka on port 9092

### Run Tests
```bash
mvn test
```

### Build JAR
```bash
mvn clean package -DskipTests
```

### Build Native Image (requires GraalVM)
```bash
mvn native:compile
```

## API Endpoints

### Integration Sources
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/sources` | Create integration source |
| GET | `/api/v1/sources` | List all sources |
| GET | `/api/v1/sources/{id}` | Get source by ID |
| PUT | `/api/v1/sources/{id}` | Update source |
| DELETE | `/api/v1/sources/{id}` | Delete source |

### Security Events
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/events` | Ingest security event |
| GET | `/api/v1/events` | List events (filterable) |
| GET | `/api/v1/events/{id}` | Get event by ID |

### Pipelines
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/pipelines` | Create pipeline |
| GET | `/api/v1/pipelines` | List all pipelines |
| GET | `/api/v1/pipelines/{id}` | Get pipeline by ID |

## Project Structure

```
src/main/java/com/github/dziki/talenthop/
├── TalentHopIntegrationApplication.java  # Main class
├── config/
│   ├── JpaConfig.java                    # JPA configuration
│   └── OpenApiConfig.java                # Swagger/OpenAPI config
├── controller/
│   ├── IntegrationSourceController.java  # Source CRUD
│   └── SecurityEventController.java      # Event endpoints
├── dto/
│   ├── IntegrationSourceRequest.java     # Create/Update request
│   ├── IntegrationSourceResponse.java    # Response DTO
│   └── SecurityEventResponse.java        # Event response
├── exception/
│   └── GlobalExceptionHandler.java       # Global error handling
├── model/
│   ├── IntegrationSource.java            # Source entity
│   ├── IntegrationPipeline.java          # Pipeline entity
│   └── SecurityEvent.java                # Event entity
├── repository/
│   ├── IntegrationSourceRepository.java
│   ├── IntegrationPipelineRepository.java
│   └── SecurityEventRepository.java
└── service/
    ├── IntegrationSourceService.java
    └── SecurityEventService.java
```

## Configuration

| Profile | Database | Kafka | Port |
|---------|----------|-------|------|
| default (dev) | H2 in-memory | localhost:9092 | 8095 |
| prod | PostgreSQL | configurable | 8095 |

### Environment Variables (prod)
| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | talenthop | Database name |
| `DB_USER` | talenthop | Database user |
| `DB_PASSWORD` | talenthop | Database password |
| `KAFKA_BOOTSTRAP_SERVERS` | kafka:29092 | Kafka bootstrap servers |

## License

MIT
