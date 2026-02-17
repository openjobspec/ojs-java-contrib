# OJS Micronaut Integration

Bean factory integration for [Open Job Spec](https://github.com/openjobspec/ojs-java-sdk) with Micronaut.

## Features

- `@OjsJob` annotation for declarative handler registration
- `@Factory`-produced `OJSClient` and `OJSWorker` beans
- `@ConfigurationProperties("ojs")` for externalized configuration
- Micronaut health indicator for OJS backend

## Installation

```kotlin
implementation("org.openjobspec:ojs-micronaut:0.1.0")
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
@Singleton
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

## Examples

See [examples/](./examples/) for a complete Micronaut application with Docker Compose.
