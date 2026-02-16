# OJS Spring Boot Example

A complete Spring Boot application demonstrating OJS integration.

## Prerequisites

- Java 21+
- Docker and Docker Compose

## Running

```bash
# Start OJS backend and Redis
docker compose up -d

# Run the application
../../../gradlew :ojs-spring:examples:bootRun
```

## Endpoints

- `POST /jobs/email` — Enqueue an email job
- `GET /actuator/health` — Health check including OJS backend status

## Testing

```bash
curl -X POST http://localhost:8081/jobs/email \
  -H "Content-Type: application/json" \
  -d '{"to": "user@example.com", "subject": "Hello"}'
```
