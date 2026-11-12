# TalentHop Integration Platform

AI-powered security operations integration platform built with Spring Boot 3.4 and Java 21.

## Features

- **Integration Source Management** — CRUD API for security data sources (SIEM, firewall, endpoint, cloud)
- **Security Event Processing** — Ingest, store, and query security events from multiple sources
- **Event Transformation** — Pipeline-based data normalization and enrichment
- **Kafka Integration** — Async event processing via Kafka
- **REST API** — Clean, versioned REST endpoints
- **GraalVM Native Image** — Optimized for fast startup and low memory footprint

## Tech Stack

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Data JPA**
- **Kafka**
- **PostgreSQL** (production) / **H2** (dev/test)
- **Lombok**
- **GraalVM Native Image**

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker (optional, for PostgreSQL and Kafka)

### Build

```bash
mvn clean install
```

### Run (development)

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` with an in-memory H2 database.

### Run (production)

```bash
# Set environment variables for PostgreSQL
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/talenthop
export SPRING_DATASOURCE_USERNAME=talenthop
export SPRING_DATASOURCE_PASSWORD=talenthop

mvn spring-boot:run
```

### Build Native Image (GraalVM)

```bash
# Install GraalVM Native Image
sudo apt install graalvm-native-image

# Build native executable
mvn native:compile

# Run native executable
./target/talenthop-integration
```

## API Endpoints

### Integration Sources

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/sources` | Create a new integration source |
| GET | `/api/v1/sources` | List all sources |
| GET | `/api/v1/sources/{id}` | Get source by ID |
| GET | `/api/v1/sources/name/{name}` | Get source by name |
| PUT | `/api/v1/sources/{id}` | Update a source |
| DELETE | `/api/v1/sources/{id}` | Delete a source |

### Security Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/events` | List all events |
| GET | `/api/v1/events/source/{sourceId}` | Get unprocessed events by source |
| GET | `/api/v1/events/severity/{severity}` | Get unprocessed events by severity |
| GET | `/api/v1/events/unprocessed?since={timestamp}` | Get unprocessed events since timestamp |
| GET | `/api/v1/events/stats` | Get event statistics |
| PATCH | `/api/v1/events/{id}/process` | Mark event as processed |

## Example Requests

### Create Integration Source

```bash
curl -X POST http://localhost:8080/api/v1/sources \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Splunk SIEM",
    "type": "SIEM",
    "endpoint": "https://splunk.example.com",
    "apiKey": "your-api-key"
  }'
```

### Get Event Stats

```bash
curl http://localhost:8080/api/v1/events/stats
```

Response:
```json
{
  "totalEvents": 150,
  "criticalEvents": 5,
  "highEvents": 20,
  "mediumEvents": 45,
  "lowEvents": 80
}
```

## Project Structure

```
src/main/java/com/github/dziki/talenthop/
├── TalentHopIntegrationApplication.java    # Main application class
├── config/
│   └── JpaConfig.java                      # JPA auditing configuration
├── controller/
│   ├── IntegrationSourceController.java    # REST API for sources
│   └── SecurityEventController.java        # REST API for events
├── dto/
│   ├── IntegrationSourceRequest.java       # Request DTO
│   ├── IntegrationSourceResponse.java      # Response DTO
│   └── SecurityEventResponse.java          # Response DTO
├── exception/
│   └── GlobalExceptionHandler.java         # Global exception handling
├── model/
│   ├── IntegrationSource.java              # JPA entity
│   ├── IntegrationPipeline.java            # JPA entity
│   └── SecurityEvent.java                  # JPA entity
├── repository/
│   ├── IntegrationSourceRepository.java    # Spring Data JPA
│   ├── IntegrationPipelineRepository.java  # Spring Data JPA
│   └── SecurityEventRepository.java        # Spring Data JPA
└── service/
    ├── IntegrationSourceService.java       # Business logic
    └── SecurityEventService.java           # Business logic
```

## License

MIT
