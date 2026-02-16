# Open Job Spec — Java Contrib

[![CI](https://github.com/openjobspec/ojs-java-contrib/actions/workflows/ci.yml/badge.svg)](https://github.com/openjobspec/ojs-java-contrib/actions/workflows/ci.yml)

Community framework integrations for the [OJS Java SDK](https://github.com/openjobspec/ojs-java-sdk).

## Provided Integrations

| Status | Integration | Description |
|--------|-------------|-------------|
| alpha  | [Spring Boot](./ojs-spring/README.md) | Auto-configuration starter with `@OjsJob`, `@Transactional` enqueue, and Actuator health |
| alpha  | [Quarkus](./ojs-quarkus/README.md) | CDI extension with `@OjsJob` and MicroProfile Health integration |
| alpha  | [Micronaut](./ojs-micronaut/README.md) | Bean factory with `@OjsJob` and health indicator |

Status definitions: `alpha` (API may change), `beta` (API stable, not battle-tested), `stable` (production-ready).

## Getting Started

Add any integration as a Gradle dependency:

```kotlin
implementation("org.openjobspec:ojs-spring:0.1.0")
```

Each module includes an `examples/` directory with a complete working demo using Docker Compose.

## Building

```bash
./gradlew build
```

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines on adding new contrib packages.

## License

Apache 2.0 — see [LICENSE](./LICENSE).
