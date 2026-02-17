package org.openjobspec.spring;

import org.openjobspec.ojs.Job;
import org.openjobspec.ojs.JobRequest;
import org.openjobspec.ojs.OJSClient;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Spring-style template class for OJS job operations. Provides a convenient,
 * high-level API following Spring conventions (similar to JdbcTemplate, RestTemplate).
 *
 * <pre>{@code
 * @Autowired OjsTemplate ojs;
 *
 * // Simple enqueue
 * ojs.enqueue("email.send", List.of("user@example.com", "Welcome!"));
 *
 * // Enqueue with args map
 * ojs.enqueue("email.send", Map.of("to", "user@example.com"));
 *
 * // Schedule for later
 * ojs.enqueueAt("report.generate", Instant.now().plus(1, HOURS), List.of(reportId));
 *
 * // Enqueue with delay
 * ojs.enqueueWithDelay("cleanup.run", Duration.ofMinutes(30), Map.of("scope", "temp"));
 *
 * // Enqueue to specific queue
 * ojs.enqueueToQueue("email.send", "high-priority", Map.of("to", "vip@example.com"));
 * }</pre>
 */
public class OjsTemplate {

    private final OJSClient client;
    private final String defaultQueue;

    public OjsTemplate(OJSClient client, String defaultQueue) {
        this.client = client;
        this.defaultQueue = defaultQueue;
    }

    /** Get the underlying OJS client for advanced operations. */
    public OJSClient getClient() {
        return client;
    }

    /**
     * Enqueue a job immediately with map arguments.
     *
     * @param type the job type (e.g. "email.send")
     * @param args the job arguments as a map
     * @return the created job
     */
    public Job enqueue(String type, Map<String, Object> args) {
        return client.enqueue(type, args);
    }

    /**
     * Enqueue a job immediately with list arguments.
     *
     * @param type the job type
     * @param args the job arguments as a list
     * @return the created job
     */
    public Job enqueue(String type, List<Object> args) {
        return client.enqueue(type, Map.of("_args", args));
    }

    /**
     * Enqueue a job to a specific queue.
     *
     * @param type  the job type
     * @param queue the target queue
     * @param args  the job arguments
     * @return the created job
     */
    public Job enqueueToQueue(String type, String queue, Map<String, Object> args) {
        return client.enqueue(type, (Object) args)
                .queue(queue)
                .send();
    }

    /**
     * Schedule a job for execution at a specific time.
     *
     * @param type        the job type
     * @param scheduledAt when to execute the job
     * @param args        the job arguments as a map
     * @return the created job
     */
    public Job enqueueAt(String type, Instant scheduledAt, Map<String, Object> args) {
        return client.enqueue(type, (Object) args)
                .queue(defaultQueue)
                .scheduledAt(scheduledAt)
                .send();
    }

    /**
     * Schedule a job for execution at a specific time with list arguments.
     *
     * @param type        the job type
     * @param scheduledAt when to execute the job
     * @param args        the job arguments as a list
     * @return the created job
     */
    public Job enqueueAt(String type, Instant scheduledAt, List<Object> args) {
        return client.enqueue(type, (Object) Map.of("_args", args))
                .queue(defaultQueue)
                .scheduledAt(scheduledAt)
                .send();
    }

    /**
     * Enqueue a job with a delay from now.
     *
     * @param type  the job type
     * @param delay the delay duration
     * @param args  the job arguments
     * @return the created job
     */
    public Job enqueueWithDelay(String type, Duration delay, Map<String, Object> args) {
        return client.enqueue(type, (Object) args)
                .queue(defaultQueue)
                .delay(delay)
                .send();
    }

    /**
     * Get a job by ID.
     *
     * @param id the job ID (UUIDv7)
     * @return the job
     */
    public Job getJob(String id) {
        return client.getJob(id);
    }

    /**
     * Cancel a job.
     *
     * @param id the job ID
     * @return the cancelled job
     */
    public Job cancelJob(String id) {
        return client.cancelJob(id);
    }

    /**
     * Enqueue multiple jobs atomically.
     *
     * @param requests the job requests as wire-format maps
     * @return list of created jobs
     */
    public List<Job> enqueueBatch(List<Map<String, Object>> requests) {
        return client.enqueueBatch(requests);
    }

    /**
     * Check server health.
     *
     * @return health status as a map
     */
    public Map<String, Object> health() {
        return client.health();
    }
}
