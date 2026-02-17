package org.openjobspec.spring;

import org.openjobspec.ojs.Job;
import org.openjobspec.ojs.JobContext;

import java.util.List;
import java.util.Map;

/**
 * Spring wrapper around the SDK's {@link JobContext}, providing convenience methods
 * for job execution within the Spring context.
 *
 * <pre>{@code
 * public Object execute(OjsJobContext ctx) {
 *     String type = ctx.jobType();
 *     int attempt = ctx.attempt();
 *     Map<String, Object> args = ctx.argsMap();
 *     ctx.heartbeat(); // extend visibility timeout
 *     return Map.of("processed", true);
 * }
 * }</pre>
 */
public final class OjsJobContext {

    private final JobContext delegate;

    public OjsJobContext(JobContext delegate) {
        this.delegate = delegate;
    }

    /** The underlying SDK job context. */
    public JobContext unwrap() {
        return delegate;
    }

    /** The job being processed. */
    public Job job() {
        return delegate.job();
    }

    /** The job type (e.g. "email.send"). */
    public String jobType() {
        return delegate.job().type();
    }

    /** The job ID. */
    public String jobId() {
        return delegate.job().id();
    }

    /** Current attempt number (1-indexed). */
    public int attempt() {
        return delegate.attempt();
    }

    /** The queue this job was fetched from. */
    public String queue() {
        return delegate.queue();
    }

    /** Job arguments as a list. */
    public List<Object> args() {
        return delegate.job().args();
    }

    /** Job arguments as a map (convenience for single-map args). */
    public Map<String, Object> argsMap() {
        return delegate.job().argsMap();
    }

    /** Job metadata. */
    public Map<String, Object> meta() {
        return delegate.job().meta();
    }

    /** Check if this job has been cancelled. */
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    /**
     * Send a heartbeat to extend the visibility timeout for long-running jobs.
     */
    public void heartbeat() {
        delegate.heartbeat();
    }

    /**
     * Set the result of this job execution.
     */
    public void setResult(Object result) {
        delegate.setResult(result);
    }
}
