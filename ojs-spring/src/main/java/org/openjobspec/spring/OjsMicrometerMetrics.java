package org.openjobspec.spring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.JobHandler;
import org.openjobspec.ojs.Middleware;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Micrometer integration for OJS job metrics. Registers as middleware on the
 * worker to automatically track job execution metrics.
 *
 * <p><b>Metrics published:</b></p>
 * <ul>
 *   <li>{@code ojs.jobs.enqueued} — counter of jobs enqueued via OjsTemplate</li>
 *   <li>{@code ojs.jobs.completed} — counter of successfully completed jobs (tag: type)</li>
 *   <li>{@code ojs.jobs.failed} — counter of failed jobs (tag: type)</li>
 *   <li>{@code ojs.jobs.active} — gauge of currently active jobs</li>
 *   <li>{@code ojs.jobs.duration} — timer of job execution duration (tag: type)</li>
 * </ul>
 */
public class OjsMicrometerMetrics implements Middleware {

    private final MeterRegistry registry;
    private final Counter enqueuedCounter;
    private final AtomicInteger activeGauge;

    public OjsMicrometerMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.enqueuedCounter = Counter.builder("ojs.jobs.enqueued")
                .description("Total jobs enqueued")
                .register(registry);
        this.activeGauge = registry.gauge("ojs.jobs.active",
                new AtomicInteger(0));
    }

    /** Increment the enqueued counter (called by OjsTemplate or application code). */
    public void recordEnqueue() {
        enqueuedCounter.increment();
    }

    /**
     * Middleware apply method — wraps job execution to record metrics.
     */
    @Override
    public void apply(JobContext ctx, JobHandler next) throws Exception {
        String jobType = ctx.job().type();
        Timer timer = Timer.builder("ojs.jobs.duration")
                .tag("type", jobType)
                .description("Job execution duration")
                .register(registry);

        activeGauge.incrementAndGet();
        Timer.Sample sample = Timer.start(registry);
        try {
            next.handle(ctx);
            Counter.builder("ojs.jobs.completed")
                    .tag("type", jobType)
                    .description("Successfully completed jobs")
                    .register(registry)
                    .increment();
        } catch (Exception e) {
            Counter.builder("ojs.jobs.failed")
                    .tag("type", jobType)
                    .description("Failed jobs")
                    .register(registry)
                    .increment();
            throw e;
        } finally {
            sample.stop(timer);
            activeGauge.decrementAndGet();
        }
    }
}
