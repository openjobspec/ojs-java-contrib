package org.openjobspec.spring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.Job;
import org.openjobspec.ojs.JobContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OjsMicrometerMetricsTest {

    private MeterRegistry registry;
    private OjsMicrometerMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new OjsMicrometerMetrics(registry);
    }

    @Test
    void recordsEnqueueCounter() {
        metrics.recordEnqueue();
        metrics.recordEnqueue();

        var counter = registry.find("ojs.jobs.enqueued").counter();
        assertNotNull(counter);
        assertEquals(2.0, counter.count());
    }

    @Test
    void recordsCompletedJobMetrics() throws Exception {
        var ctx = createJobContext("email.send");

        metrics.apply(ctx, innerCtx -> null);

        var completed = registry.find("ojs.jobs.completed").tag("type", "email.send").counter();
        assertNotNull(completed);
        assertEquals(1.0, completed.count());

        var duration = registry.find("ojs.jobs.duration").tag("type", "email.send").timer();
        assertNotNull(duration);
        assertEquals(1, duration.count());
    }

    @Test
    void recordsFailedJobMetrics() {
        var ctx = createJobContext("report.generate");

        assertThrows(RuntimeException.class, () ->
                metrics.apply(ctx, innerCtx -> {
                    throw new RuntimeException("boom");
                }));

        var failed = registry.find("ojs.jobs.failed").tag("type", "report.generate").counter();
        assertNotNull(failed);
        assertEquals(1.0, failed.count());
    }

    @Test
    void activeGaugeIncrementsDuringExecution() throws Exception {
        var ctx = createJobContext("test.job");
        var activeGauge = registry.find("ojs.jobs.active").gauge();
        assertNotNull(activeGauge);
        assertEquals(0.0, activeGauge.value());

        metrics.apply(ctx, innerCtx -> {
            var gauge = registry.find("ojs.jobs.active").gauge();
            assertEquals(1.0, gauge.value());
            return null;
        });

        assertEquals(0.0, activeGauge.value());
    }

    private static JobContext createJobContext(String jobType) {
        var job = new Job(Job.SPEC_VERSION, "test-id", jobType, "default",
                List.of(), Map.of(), 0, 0, null, null, null, null, null,
                "active", 1, null, null, null, null, null, null, List.of());
        var ctx = mock(JobContext.class);
        when(ctx.job()).thenReturn(job);
        return ctx;
    }
}
