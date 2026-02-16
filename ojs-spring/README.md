# OJS Spring Boot Starter

Auto-configuration starter for [Open Job Spec](https://github.com/openjobspec/ojs-java-sdk) with Spring Boot.

## Features

- `@OjsJob` annotation for declarative handler registration
- Auto-configured `OJSClient` and `OJSWorker` beans
- `@ConfigurationProperties("ojs")` for externalized configuration
- `@Transactional`-aware enqueue via `OjsTransactionalEnqueue`
- Spring Actuator health indicator

## Installation

```kotlin
implementation("org.openjobspec:ojs-spring:0.1.0")
```

## Quick Start

```yaml
# application.yml
ojs:
  url: http://localhost:8080
  queues:
    - default
    - email
  concurrency: 10
```

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

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `ojs.url` | `http://localhost:8080` | OJS backend URL |
| `ojs.queues` | `[default]` | Queues to poll |
| `ojs.concurrency` | `10` | Worker concurrency |
| `ojs.enabled` | `true` | Enable/disable OJS auto-configuration |

## Examples

See [examples/](./examples/) for a complete Spring Boot application with Docker Compose.
