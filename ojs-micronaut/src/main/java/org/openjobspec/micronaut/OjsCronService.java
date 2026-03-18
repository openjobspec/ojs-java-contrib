package org.openjobspec.micronaut;

import jakarta.inject.Singleton;
import org.openjobspec.ojs.OJSClient;

import java.util.List;
import java.util.Map;

/**
 * Micronaut service for managing OJS cron jobs.
 *
 * <p>Wraps the {@link OJSClient} cron API with a clean, injectable service interface.</p>
 *
 * <pre>{@code
 * @Inject OjsCronService cron;
 *
 * cron.register("daily-cleanup", "0 0 * * *", "cleanup.run",
 *     Map.of("maxAge", 30));
 * }</pre>
 */
@Singleton
public class OjsCronService {

    private final OJSClient client;

    public OjsCronService(OJSClient client) {
        this.client = client;
    }

    /** Lists all registered cron jobs. */
    public List<Map<String, Object>> list() {
        return client.listCronJobs();
    }

    /**
     * Registers a cron job with the given name, schedule expression, job type, and arguments.
     *
     * @param name     unique cron job name
     * @param schedule cron expression (e.g. {@code "0 * * * *"})
     * @param jobType  the OJS job type to enqueue on trigger
     * @param args     job arguments
     * @return the registered cron job descriptor
     */
    public Map<String, Object> register(String name, String schedule,
                                        String jobType, Map<String, Object> args) {
        return client.registerCronJob(Map.of(
                "name", name,
                "schedule", schedule,
                "type", jobType,
                "args", args
        ));
    }

    /**
     * Registers a cron job with the given name, schedule, job type, arguments, and queue.
     *
     * @param name     unique cron job name
     * @param schedule cron expression
     * @param jobType  the OJS job type to enqueue on trigger
     * @param args     job arguments
     * @param queue    target queue for the enqueued jobs
     * @return the registered cron job descriptor
     */
    public Map<String, Object> register(String name, String schedule,
                                        String jobType, Map<String, Object> args,
                                        String queue) {
        return client.registerCronJob(Map.of(
                "name", name,
                "schedule", schedule,
                "type", jobType,
                "args", args,
                "queue", queue
        ));
    }

    /** Unregisters a cron job by name. */
    public void unregister(String name) {
        client.unregisterCronJob(name);
    }
}
