# OJS Quarkus Extension

CDI extension for [Open Job Spec](https://github.com/openjobspec/ojs-java-sdk) with Quarkus.

## Features

- `@OjsJob` annotation for declarative handler registration
- CDI-produced `OJSClient` and `OJSWorker` beans
- Quarkus `@ConfigMapping` for type-safe configuration
- MicroProfile Health check for OJS backend

## Installation

```kotlin
implementation("org.openjobspec:ojs-quarkus:0.1.0")
```

## Quick Start

```properties
# application.properties
ojs.url=http://localhost:8080
ojs.queues=default,email
ojs.concurrency=10
```

```java
@ApplicationScoped
public class EmailJob {
    @OjsJob("email.send")
    public Object handle(JobContext ctx) {
        var to = (String) ctx.job().argsMap().get("to");
        // send email...
        return Map.of("sent", true);
    }
}
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `ojs.url` | `http://localhost:8080` | OJS backend URL |
| `ojs.queues` | `default` | Comma-separated queues to poll |
| `ojs.concurrency` | `10` | Worker concurrency |

## Examples

See [examples/](./examples/) for a complete Quarkus application with Docker Compose.
