# Contributing to OJS Java Contrib

Thank you for your interest in contributing to OJS Java Contrib!

## Adding a New Integration Module

1. **Create a directory** named `ojs-{framework}/` at the repository root.

2. **Required files:**
   - `README.md` — Module name, installation, quick usage, API summary, link to examples
   - `build.gradle.kts` — Module build with `org.openjobspec:ojs-sdk` dependency
   - `src/main/java/org/openjobspec/{framework}/` — Integration code
   - `src/test/java/org/openjobspec/{framework}/` — Unit tests (JUnit 5, mock HTTP)
   - `examples/` — Complete working example with Docker Compose

3. **Add the module to `settings.gradle.kts`** at the repository root.

4. **Update the root `README.md`** status table with your new integration.

5. **Update the CI matrix** in `.github/workflows/ci.yml`.

## Module Guidelines

- Keep dependencies minimal: only the framework + OJS SDK.
- Use idiomatic patterns for the target framework.
- Provide an `@OjsJob` annotation for consistent handler registration across frameworks.
- Include auto-configuration or factory that creates `OJSClient` and `OJSWorker` beans.
- Provide a health check using the framework's health indicator pattern.
- Tests should use JUnit 5 with mocked HTTP — no real OJS backend required.

## Example Guidelines

Each example should include:
- `docker-compose.yml` with `ojs-backend-redis` and Redis for integration demos
- `build.gradle.kts` with dependency on the parent module
- Complete application code demonstrating job enqueue and processing
- `README.md` — Prerequisites, setup, and run instructions

## Code Style

- Use Java 21 features: records, sealed interfaces, pattern matching.
- Follow framework-specific conventions for each integration.
- Document all public types and methods.

## Pull Request Process

1. Fork the repository and create a feature branch.
2. Ensure all tests pass: `./gradlew test`
3. Submit a pull request with a clear description.
