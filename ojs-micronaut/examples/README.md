# OJS Micronaut Example

A complete Micronaut application demonstrating OJS integration.

## Prerequisites

- Java 21+
- Docker and Docker Compose

## Running

```bash
# Start OJS backend and Redis
docker compose up -d

# Run the application
../../../gradlew :ojs-micronaut:examples:run
```

## Endpoints

- `POST /jobs/email` — Enqueue an email job
- `GET /health` — Health check including OJS backend status
