# Clean-Code / SRP Audit — `ojs-java-contrib`

Branch: `refactor/clean-code-srp` (base: `main`). Scope: Spring Boot starter (`ojs-spring`),
Quarkus extension (`ojs-quarkus`), Micronaut integration (`ojs-micronaut`). Declared SDK
contract consumed: `org.openjobspec:ojs-sdk:0.5.0`, Java 21 toolchain, Gradle 8.8.

## Summary

- The three framework adapters are already small, cohesive, and well-tested (baseline 306
  tests). The dominant defect is a **stale baseline test**, not pervasive SRP rot — this audit
  is deliberately surgical rather than a large re-architecture.
- **Baseline was red** because `OjsEncryptionAutoConfigurationTest` assumed
  `EncryptionMiddleware` is *absent* ("not on the classpath in v0.2.0"), but the current
  declared contract **SDK 0.5.0 ships `EncryptionMiddleware`**, so the reflective wiring now
  succeeds and no `IllegalStateException` is thrown. Repaired by asserting the real runtime
  behaviour (both middlewares registered) — coverage strengthened, nothing skipped.
- **Highest-leverage split:** extract the reflective method-invocation mechanics out of
  `OjsJobRegistrar` into a dedicated package-private `ReflectiveJobHandler` (Spring). This
  removes the "reflection actor" from the bean-post-processor so the registrar owns only
  *scanning + registration*, and yields an independently testable unit (access handling +
  `InvocationTargetException` unwrapping).
- **One latent correctness bug** fixed: the method-level `@OjsJob` path in `OjsJobRegistrar`
  registered handlers even when the resolved job type was empty, while the class-level path
  guarded against it — an inconsistency that could register a handler under an empty type.
- Cross-framework duplication (the `invokeHandler` reflection block repeated in Spring /
  Quarkus / Micronaut) is left **intentionally un-normalized**: those are independent per-actor
  adapters and must not be merged across framework boundaries. All public APIs, annotations,
  auto-config metadata, config keys, routes, and wire formats are preserved.

## Findings

| ID | Location | Category | Severity | Actors in conflict | Cost | Size | Behavior risk |
|----|----------|----------|----------|--------------------|------|------|---------------|
| F1 | `ojs-spring` `OjsEncryptionAutoConfigurationTest.throwsWhenEncryptionMiddlewareClassNotAvailable` | Stale test / broken baseline | **P0** | SDK-contract owner (0.5.0 ships `EncryptionMiddleware`) vs test author (assumed 0.2.0 absence) | S | ~14 LOC (test) | None — test-only; now asserts real runtime behaviour |
| F2 | `ojs-spring` `OjsJobRegistrar.postProcessAfterInitialization` (method-level loop) | Inconsistent guard / latent defect | **P1** | Method-level registration path vs class-level path (same class, divergent empty-type handling) | S | 1 guard + 1 regression test | Low — only changes the empty-type edge case (previously a mis-registration) |
| F3 | `ojs-spring` `OjsJobRegistrar` → new `ReflectiveJobHandler` | SRP / mixed abstraction (scanning vs reflection mechanics) | **P2** (highest-leverage split) | Bean-scanning/registration actor vs reflective-invocation actor | M | new 45-LOC unit + 4 unit tests | Low — identical invocation semantics; characterized by tests |
| F4 | `ojs-spring` `OjsWorkflowTemplate.batch(...)` and `WorkflowBuilder.dispatch()` | Actor-aware duplication (DRY) | **P2** | Direct template API vs fluent builder (both assemble batch callbacks) | S | 1 private helper, 2 call sites | None — pure extraction; existing tests characterize both paths |

Severity key: P0 = broken build/gate, P1 = correctness defect, P2 = maintainability/SRP.
Cost: S ≤ ~15 LOC, M ≤ ~60 LOC. All findings are within a single actor (`ojs-spring`); no
cross-framework normalization was performed.

### Detail & rationale

- **F1** — `EncryptionMiddleware`, `EncryptionMiddleware$EncryptionCodec`,
  `$StaticKeyProvider`, `$KeyProvider` and the static `encryptionMiddleware` /
  `decryptionMiddleware(codec, keyProvider)` factories are all present in `ojs-sdk-0.5.0.jar`
  (verified via `javap`). The reflection in `OjsEncryptionAutoConfiguration` therefore resolves
  every class/method and calls `worker.use("ojs-encrypt", …)` / `worker.use("ojs-decrypt", …)`.
  The old assertion (`IllegalStateException`) described a `ClassNotFoundException` branch that is
  unreachable under the declared contract (and doubly so at runtime because the bean is guarded
  by `@ConditionalOnClass(name = "…EncryptionMiddleware")`). The rewrite verifies both
  middlewares are installed — a path that was previously **untested**.
- **F2** — class-level registration already guards `if (!jobType.isEmpty())`; the method-level
  loop did not, so `@OjsJob` with no `type`/`value` (both default to `""` in the Spring
  annotation) would call `worker.register("", handler)`. The guard is now symmetric. Regression
  test `skipsMethodLevelAnnotationWithEmptyType` locks the behaviour.
- **F3** — `OjsJobRegistrar` is a `BeanPostProcessor`; its reason to change should be "how we
  discover/register annotated beans", not "how we reflectively invoke a method". The extracted
  `ReflectiveJobHandler implements JobHandler` owns access handling and
  `InvocationTargetException` unwrapping (re-throwing the handler's own checked exception,
  wrapping a non-`Exception` `Throwable`). It is package-private (no public-API change), is not
  forwarding-only (it makes real decisions), and is covered by 4 direct unit tests. The
  equivalent blocks in Quarkus `OjsExtension` and Micronaut `OjsJobProcessor` are **left as-is**
  (separate actors).
- **F4** — the null-skipping assembly of `Workflow.CallbacksBuilder` appeared verbatim in the
  direct `batch(...)` method and in the fluent `WorkflowBuilder.dispatch()` `BATCH` branch —
  duplication within one actor. Extracted to `private static buildCallbacks(...)`. The 14
  existing `OjsWorkflowTemplateTest` cases (including `batchWithAllCallbacks` and
  `fluentBuilderBatchWithCallbacksDispatches`) characterize both call sites.

## Ordered refactor sequence

1. **F1 (P0)** — restore a green baseline first; it unblocks all downstream verification.
2. **F2 (P1)** — add the empty-type guard and its regression test; this also *characterizes*
   `OjsJobRegistrar` before its internals are extracted.
3. **F3 (P2)** — extract `ReflectiveJobHandler`, relying on the F2/existing registrar tests as
   the characterization net; add dedicated unit tests for the extracted seam.
4. **F4 (P2)** — dedup the batch-callbacks assembly (independent of the above; existing
   workflow tests characterize it).
5. **Verify** — per-module `test`, then full `clean build` (see Gates).

## Out of scope

- **O1 — Cron field naming.** `ojs-spring` `OjsCronBridge.syncFromProperties` emits the `"cron"`
  key, while `ojs-quarkus`/`ojs-micronaut` `OjsCronService` emit `"schedule"`. This is a
  wire/schema contract spanning actors; reconciling it requires the OJS HTTP-binding spec and a
  compatibility decision. Not touched.
- **O2 — `OjsProperties` (372 LOC) & dual retry surface.** It is an idiomatic Spring
  `@ConfigurationProperties` data holder; its nested types and the `Retry` vs
  `OjsRetryProperties` split are public config-key/binding surface. Splitting would break
  binding and public API.
- **O3 — Cross-framework `invokeHandler` duplication.** Present in Spring/Quarkus/Micronaut by
  design; these are independent per-actor adapters and must not be normalized across framework
  boundaries.
- **O4 — Thin delegating templates/services** (`OjsTemplate`, `OjsCronBridge`,
  `OjsWorkflowService`, `OjsProducer`, etc.). Delegation is the intended framework idiom
  (JdbcTemplate-style); they are public API, not a forwarding-only smell to remove.
- **O5 — Example projects** (`ojs-*/examples`). Independent Gradle builds, excluded from
  `settings.gradle.kts` and the CI matrix; outside this quality pass.

## Deferred

- **D1 — Reflection in Spring `OjsEncryptionAutoConfiguration`.** Now that SDK 0.5.0 guarantees
  `EncryptionMiddleware` (and Quarkus/Micronaut use it directly at compile time), the reflective
  wiring could become a direct call. Deferred: it is a defensive compatibility seam already
  guarded by `@ConditionalOnClass`, it sits on the encryption security path, and collapsing it
  is a product decision about the minimum supported SDK — not a mechanically-safe refactor.
- **D2 — Micronaut `OjsMiddlewareChain` doc vs wiring.** Its Javadoc states `OjsWorkerLifecycle`
  applies the chain at startup, but the lifecycle injects `List<Middleware>` directly and never
  calls `applyTo()`; middleware added via `chain.add(...)` *after* construction is not honored.
  Fixing this is a behaviour/public-contract choice (correct the doc vs. wire the chain into the
  lifecycle), so it is deferred rather than silently changed.
- **D3 — Micronaut `OjsHealthIndicator` reactive contract.** The `Publisher` emits
  `onNext`/`onComplete` on every `request(n)` rather than honoring demand exactly once. Low
  practical impact (single-subscription health checks), covered by an existing test, and risky
  to alter; deferred.

## Gates (actual results on this branch)

Environment: JDK 21 (Zulu 21.0.7), Gradle 8.8, `ojs-sdk:0.5.0`.

| Gate | Command | Result |
|------|---------|--------|
| Baseline (before) | `./gradlew check` | **FAIL** — `ojs-spring:test` 83 run, 1 failed (`OjsEncryptionAutoConfigurationTest`) |
| Full clean build (after) | `./gradlew clean build` | **PASS** — 28 tasks, all modules assembled (incl. sourcesJar/javadocJar) |
| Spring tests | `./gradlew :ojs-spring:test` | **PASS** — 88 tests, 0 failures, 0 errors, 0 skipped |
| Quarkus tests | `./gradlew :ojs-quarkus:test` | **PASS** — 112 tests, 0 failures, 0 errors, 0 skipped |
| Micronaut tests | `./gradlew :ojs-micronaut:test` | **PASS** — 111 tests, 0 failures, 0 errors, 0 skipped |
| Lint / static analysis | none declared | No Spotless/Checkstyle/PMD/SpotBugs configured; `check` == `test` |
| `dependencyCheckAnalyze` (CI) | `./gradlew :<mod>:dependencyCheckAnalyze` | Task **does not exist** (OWASP plugin not applied); CI marks this step `continue-on-error: true` — advisory, not a canonical gate |

Canonical gates: `build`, `test`, `check`. Total after: **311 tests, 0 failures** (was 306
runnable with 1 failing). No dependency versions changed; no native/AOT build is configured in
this repo. Changes are confined to `ojs-spring/` and left unstaged.
