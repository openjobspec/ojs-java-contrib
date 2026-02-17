# OJS Spring Boot Starter

Auto-configuration starter for [Open Job Spec](https://github.com/openjobspec/ojs-java-sdk) with Spring Boot 3.2+.

## Features

- **`@OjsJob` annotation** — Declarative handler registration (method-level and class-level)
- **`OjsJobHandler` interface** — Type-safe job handler contract with `OjsJobContext`
- **Auto-configured beans** — `OJSClient`, `OJSWorker`, `OjsTemplate` ready to inject
- **`OjsTemplate`** — Spring-style template for enqueuing jobs (like `JdbcTemplate`, `RestTemplate`)
- **`@ConfigurationProperties("ojs")`** — Externalized configuration with IDE hints
- **`@Transactional`-aware** — `OjsTransactionalEnqueue` defers enqueue until after commit
- **Spring Actuator** — Health indicator with server connectivity, queue depths, worker status
- **Micrometer metrics** — Counters, timers, and gauges for job processing observability

## Installation

### Maven

```xml
<dependency>
    <groupId>org.openjobspec</groupId>
    <artifactId>ojs-spring</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```kotlin
implementation("org.openjobspec:ojs-spring:0.1.0")
```

## Quick Start

### 1. Configure

```yaml
# application.yml
ojs:
  url: http://localhost:8080
  default-queue: default
  worker:
    concurrency: 10
    queues:
      - default
      - email
  retry:
    max-attempts: 3
    backoff: exponential
```

### 2. Define Job Handlers

**Method-level (simple):**

```java
@Component
public class EmailJob {
    @OjsJob("email.send")
    public Object handle(JobContext ctx) {
        var to = (String) ctx.job().argsMap().get("to");
        // send email...
        return Map.of("sent", true);
    }
}
```

**Class-level (type-safe):**

```java
@OjsJob(type = "email.send", queue = "emails")
@Component
public class SendEmailJob implements OjsJobHandler {
    @Autowired
    private EmailService emailService;

    @Override
    public Object execute(OjsJobContext ctx) {
        String to = (String) ctx.argsMap().get("to");
        emailService.send(to, (String) ctx.argsMap().get("subject"));
        return Map.of("sent", true);
    }
}
```

### 3. Enqueue Jobs

**Using OjsTemplate:**

```java
@Service
public class OrderService {
    @Autowired
    private OjsTemplate ojs;

    public void processOrder(Order order) {
        // Simple enqueue
        ojs.enqueue("email.send", Map.of("to", order.email(), "subject", "Order Confirmed"));

        // Schedule for later
        ojs.enqueueAt("report.generate", Instant.now().plus(1, HOURS), Map.of("orderId", order.id()));

        // Enqueue with delay
        ojs.enqueueWithDelay("cleanup.run", Duration.ofMinutes(30), Map.of("scope", "temp"));

        // Enqueue to specific queue
        ojs.enqueueToQueue("email.send", "high-priority", Map.of("to", "vip@example.com"));
    }
}
```

**Using transactional enqueue:**

```java
@Service
public class OrderService {
    @Autowired
    private OjsTransactionalEnqueue enqueue;

    @Transactional
    public void createOrder(Order order) {
        orderRepo.save(order);
        // Job enqueued only after transaction commits
        enqueue.afterCommit("order.process", Map.of("orderId", order.id()));
    }
}
```

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `ojs.url` | `http://localhost:8080` | OJS backend URL |
| `ojs.default-queue` | `default` | Default queue for enqueued jobs |
| `ojs.queues` | `[default]` | Queues to poll (shorthand for `ojs.worker.queues`) |
| `ojs.concurrency` | `10` | Worker concurrency (shorthand for `ojs.worker.concurrency`) |
| `ojs.enabled` | `true` | Enable/disable OJS auto-configuration |
| `ojs.worker.concurrency` | — | Worker thread count (overrides top-level) |
| `ojs.worker.queues` | — | Worker queues (overrides top-level) |
| `ojs.retry.max-attempts` | `3` | Maximum retry attempts |
| `ojs.retry.backoff` | `exponential` | Backoff strategy: `exponential` or `fixed` |

### Profile-specific Configuration

```yaml
# application.yml (shared)
ojs:
  url: http://localhost:8080

---
# application-dev.yml
ojs:
  worker:
    concurrency: 2
    queues: [default]

---
# application-prod.yml
ojs:
  url: ${OJS_URL:http://ojs-server:8080}
  worker:
    concurrency: 50
    queues: [default, email, reports, critical]
  retry:
    max-attempts: 5
```

## Actuator Health Check

When Spring Boot Actuator is on the classpath, an OJS health indicator is automatically registered:

```
GET /actuator/health
```

```json
{
  "status": "UP",
  "components": {
    "ojs": {
      "status": "UP",
      "details": {
        "status": "ok",
        "worker": {
          "id": "worker_abc12345",
          "state": "running",
          "activeJobs": 3
        },
        "queues": [
          { "name": "default", "depth": 42 },
          { "name": "email", "depth": 7 }
        ]
      }
    }
  }
}
```

## Micrometer Metrics

When Micrometer is on the classpath, OJS metrics are automatically published:

| Metric | Type | Tags | Description |
|--------|------|------|-------------|
| `ojs.jobs.enqueued` | Counter | — | Total jobs enqueued |
| `ojs.jobs.completed` | Counter | `type` | Successfully completed jobs |
| `ojs.jobs.failed` | Counter | `type` | Failed jobs |
| `ojs.jobs.active` | Gauge | — | Currently active jobs |
| `ojs.jobs.duration` | Timer | `type` | Job execution duration |

## Auto-configured Beans

| Bean | Type | Condition |
|------|------|-----------|
| `ojsClient` | `OJSClient` | Always (when enabled) |
| `ojsWorker` | `OJSWorker` | Always (when enabled) |
| `ojsTemplate` | `OjsTemplate` | Always (when enabled) |
| `ojsJobRegistrar` | `OjsJobRegistrar` | Always (when enabled) |
| `ojsHealthIndicator` | `OjsHealthIndicator` | Spring Actuator on classpath |
| `ojsMicrometerMetrics` | `OjsMicrometerMetrics` | Micrometer on classpath |
| `ojsTransactionalEnqueue` | `OjsTransactionalEnqueue` | Spring TX on classpath |

All beans are `@ConditionalOnMissingBean` — provide your own to override.

## Migration Guide

### From Spring Batch

| Spring Batch | OJS Spring |
|--------------|------------|
| `@EnableBatchProcessing` | Auto-configured (no annotation needed) |
| `Job` / `Step` / `Tasklet` | `@OjsJob` / `OjsJobHandler` |
| `JobLauncher.run()` | `ojsTemplate.enqueue()` |
| `JobRepository` (DB) | OJS backend (Redis/Postgres) |
| `@Scheduled` + `Job` | `ojs.enqueueAt()` or cron jobs via OJS API |

### From Quartz

| Quartz | OJS Spring |
|--------|------------|
| `@DisallowConcurrentExecution` | Unique jobs via `UniquePolicy` |
| `JobDetail` + `Trigger` | `@OjsJob` annotation |
| `Scheduler.scheduleJob()` | `ojsTemplate.enqueueAt()` |
| `@PersistJobDataAfterExecution` | Job results stored automatically |
| Cron triggers | Cron jobs via OJS API |

## Examples

See [examples/](./examples/) for a complete Spring Boot application with Docker Compose.
