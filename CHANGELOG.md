# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.5.0] - 2026-09-02

### Changed

- Aligned all integration packages and examples with the coordinated 0.5.0
  release and `ojs-java-sdk` 0.5.0
- Pre-release CI now rejects its SDK revision placeholder until it is replaced
  with the immutable 40-character SDK commit produced by the coordinated release
- Added reproducible archives, package metadata, release automation, publish dry-run,
  wrapper verification, and consumer-smoke gates

### Fixed

- Quarkus consumers now discover the integration's CDI producers through its bean archive
- Quarkus and Micronaut examples now declare the platform metadata required for standalone builds

## [0.4.1] - 2026-04-21

### Changed

- Documentation-only follow-up to the 0.4.0 release

## [0.4.0] - 2026-04-20

### Changed

- Updated the Spring Boot, Quarkus, and Micronaut integrations
- Aligned the integrations with `ojs-java-sdk` 0.4.0

## [0.3.0] - 2026-03-09

### Added

- Middleware adapters, structured logging, metrics, and expanded integration guidance

## [0.2.0] - 2026-02-28

### Added

- Spring Boot auto-configuration and framework-specific metrics integrations

### Fixed

- Quarkus CDI scope and SDK compatibility issues

## [0.1.0] - 2026-02-23

### Added

- Initial Spring Boot, Quarkus, and Micronaut integration modules

[Unreleased]: https://github.com/openjobspec/ojs-java-contrib/compare/v0.5.0...HEAD
[0.5.0]: https://github.com/openjobspec/ojs-java-contrib/compare/v0.4.1...v0.5.0
[0.4.1]: https://github.com/openjobspec/ojs-java-contrib/compare/v0.4.0...v0.4.1
[0.4.0]: https://github.com/openjobspec/ojs-java-contrib/compare/v0.3.0...v0.4.0
[0.3.0]: https://github.com/openjobspec/ojs-java-contrib/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/openjobspec/ojs-java-contrib/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/openjobspec/ojs-java-contrib/releases/tag/v0.1.0
