package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Spring service for managing OJS cron jobs.
 *
 * <p>Wraps the SDK's cron API with Spring-friendly methods and supports
 * declarative cron definitions from {@link OjsProperties}:
 *
 * <pre>{@code
 * @Autowired OjsCronBridge cron;
 *
 * // Register a cron job programmatically
 * cron.register(Map.of(
 *     "name", "daily-report",
 *     "cron", "0 8 * * *",
 *     "type", "report.generate",
 *     "queue", "reports",
 *     "args", Map.of("scope", "daily")));
 *
 * // List registered cron jobs
 * var jobs = cron.list();
 *
 * // Unregister
 * cron.unregister("daily-report");
 * }</pre>
 *
 * <p>Declarative configuration via properties:
 * <pre>{@code
 * ojs:
 *   cron:
 *     definitions:
 *       - name: daily-report
 *         cron: "0 8 * * *"
 *         type: report.generate
 *         queue: reports
 *       - name: hourly-cleanup
 *         cron: "0 * * * *"
 *         type: cleanup.run
 * }</pre>
 */
public class OjsCronBridge {

    private final OJSClient client;

    public OjsCronBridge(OJSClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    /**
     * Register a cron job with the OJS server.
     *
     * @param cronJob the cron job definition as a map with keys: name, cron, type, queue, args
     * @return the registration response
     */
    public Map<String, Object> register(Map<String, Object> cronJob) {
        return client.registerCronJob(cronJob);
    }

    /**
     * Unregister a cron job by name.
     *
     * @param name the cron job name
     */
    public void unregister(String name) {
        client.unregisterCronJob(name);
    }

    /**
     * List all registered cron jobs.
     *
     * @return list of cron job descriptions
     */
    public List<Map<String, Object>> list() {
        return client.listCronJobs();
    }

    /**
     * Synchronize cron definitions from Spring Boot properties.
     * Registers all definitions declared in {@code ojs.cron.definitions}.
     *
     * @param definitions the cron definitions from properties
     */
    public void syncFromProperties(List<OjsProperties.CronDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return;
        }
        for (var def : definitions) {
            var cronJob = new LinkedHashMap<String, Object>();
            cronJob.put("name", def.getName());
            cronJob.put("cron", def.getCron());
            cronJob.put("type", def.getType());
            if (def.getQueue() != null && !def.getQueue().isEmpty()) {
                cronJob.put("queue", def.getQueue());
            }
            if (def.getArgs() != null && !def.getArgs().isEmpty()) {
                cronJob.put("args", def.getArgs());
            }
            client.registerCronJob(cronJob);
        }
    }
}
