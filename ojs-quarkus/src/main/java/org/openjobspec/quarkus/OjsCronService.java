package org.openjobspec.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.openjobspec.ojs.OJSClient;

import java.util.List;
import java.util.Map;

/**
 * CDI service for managing OJS cron jobs.
 *
 * <pre>{@code
 * @Inject OjsCronService cron;
 *
 * cron.register(Map.of(
 *     "name",     "daily-cleanup",
 *     "schedule", "0 3 * * *",
 *     "type",     "cleanup.run",
 *     "args",     List.of(Map.of("dryRun", false))
 * ));
 *
 * cron.list().forEach(c -> System.out.println(c.get("name")));
 * cron.unregister("daily-cleanup");
 * }</pre>
 */
@ApplicationScoped
public class OjsCronService {

    @Inject
    OJSClient client;

    /**
     * List all registered cron jobs.
     *
     * @return list of cron job descriptors
     */
    public List<Map<String, Object>> list() {
        return client.listCronJobs();
    }

    /**
     * Register (or update) a cron job.
     *
     * @param cronJob map containing at least {@code name}, {@code schedule},
     *                {@code type}, and optionally {@code args}, {@code queue},
     *                {@code meta}, {@code retry}, etc.
     * @return the server-confirmed cron job descriptor
     */
    public Map<String, Object> register(Map<String, Object> cronJob) {
        return client.registerCronJob(cronJob);
    }

    /**
     * Convenience method to register a cron job with explicit fields.
     *
     * @param name     unique cron job name
     * @param schedule cron expression (e.g. {@code "0/5 * * * *"})
     * @param jobType  the job type to enqueue
     * @param args     job arguments
     * @return the server-confirmed cron job descriptor
     */
    public Map<String, Object> register(String name, String schedule,
                                        String jobType, List<Object> args) {
        return client.registerCronJob(Map.of(
                "name", name,
                "schedule", schedule,
                "type", jobType,
                "args", args
        ));
    }

    /**
     * Unregister (remove) a cron job by name.
     *
     * @param name the cron job name to remove
     */
    public void unregister(String name) {
        client.unregisterCronJob(name);
    }
}
