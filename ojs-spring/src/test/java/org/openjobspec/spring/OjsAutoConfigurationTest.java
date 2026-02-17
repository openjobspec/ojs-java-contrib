package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.*;

class OjsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OjsAutoConfiguration.class));

    @Test
    void autoConfiguresClientAndWorker() {
        contextRunner
                .withPropertyValues("ojs.url=http://localhost:9090")
                .run(ctx -> {
                    assertTrue(ctx.containsBean("ojsClient"));
                    assertTrue(ctx.containsBean("ojsWorker"));
                    assertInstanceOf(OJSClient.class, ctx.getBean("ojsClient"));
                    assertInstanceOf(OJSWorker.class, ctx.getBean("ojsWorker"));
                });
    }

    @Test
    void disabledWhenPropertyIsFalse() {
        contextRunner
                .withPropertyValues("ojs.enabled=false")
                .run(ctx -> {
                    assertFalse(ctx.containsBean("ojsClient"));
                    assertFalse(ctx.containsBean("ojsWorker"));
                });
    }

    @Test
    void respectsCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "ojs.url=http://custom:8080",
                        "ojs.concurrency=20",
                        "ojs.queues=high,low"
                )
                .run(ctx -> {
                    var props = ctx.getBean(OjsProperties.class);
                    assertEquals("http://custom:8080", props.getUrl());
                    assertEquals(20, props.getConcurrency());
                    assertEquals(2, props.getQueues().size());
                });
    }

    @Test
    void registersJobRegistrar() {
        contextRunner
                .withPropertyValues("ojs.url=http://localhost:8080")
                .run(ctx -> assertTrue(ctx.containsBean("ojsJobRegistrar")));
    }

    @Test
    void registersOjsTemplate() {
        contextRunner
                .withPropertyValues("ojs.url=http://localhost:8080")
                .run(ctx -> {
                    assertTrue(ctx.containsBean("ojsTemplate"));
                    assertInstanceOf(OjsTemplate.class, ctx.getBean("ojsTemplate"));
                });
    }

    @Test
    void templateUsesDefaultQueue() {
        contextRunner
                .withPropertyValues(
                        "ojs.url=http://localhost:8080",
                        "ojs.default-queue=emails"
                )
                .run(ctx -> {
                    var props = ctx.getBean(OjsProperties.class);
                    assertEquals("emails", props.getDefaultQueue());
                });
    }

    @Test
    void respectsNestedWorkerProperties() {
        contextRunner
                .withPropertyValues(
                        "ojs.url=http://localhost:8080",
                        "ojs.worker.concurrency=32",
                        "ojs.worker.queues=critical,background"
                )
                .run(ctx -> {
                    var props = ctx.getBean(OjsProperties.class);
                    assertEquals(32, props.getWorker().getConcurrency());
                    assertEquals(32, props.resolvedConcurrency());
                    assertEquals(2, props.resolvedQueues().size());
                    assertTrue(props.resolvedQueues().contains("critical"));
                });
    }

    @Test
    void respectsRetryProperties() {
        contextRunner
                .withPropertyValues(
                        "ojs.url=http://localhost:8080",
                        "ojs.retry.max-attempts=5",
                        "ojs.retry.backoff=fixed"
                )
                .run(ctx -> {
                    var props = ctx.getBean(OjsProperties.class);
                    assertEquals(5, props.getRetry().getMaxAttempts());
                    assertEquals("fixed", props.getRetry().getBackoff());
                });
    }

    @Test
    void topLevelQueuesUsedWhenWorkerQueuesEmpty() {
        contextRunner
                .withPropertyValues(
                        "ojs.url=http://localhost:8080",
                        "ojs.queues=alpha,beta"
                )
                .run(ctx -> {
                    var props = ctx.getBean(OjsProperties.class);
                    assertEquals(2, props.resolvedQueues().size());
                    assertTrue(props.resolvedQueues().contains("alpha"));
                });
    }
}
