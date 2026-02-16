# OJS Quarkus Example

A complete Quarkus application demonstrating OJS integration.

## Prerequisites

- Java 21+
- Docker and Docker Compose

## Running

```bash
# Start OJS backend and Redis
docker compose up -d

# Run the application in dev mode
../../../gradlew :ojs-quarkus:examples:quarkusDev
```

## Endpoints

- `POST /jobs/email` — Enqueue an email job
- `GET /q/health/ready` — Readiness check including OJS backend status
