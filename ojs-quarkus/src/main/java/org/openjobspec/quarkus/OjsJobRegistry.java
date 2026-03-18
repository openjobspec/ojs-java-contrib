package org.openjobspec.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import org.openjobspec.ojs.JobHandler;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of OJS job handlers discovered via {@link OjsJob} annotations
 * or registered programmatically.
 *
 * <p>The {@link OjsExtension} populates this registry during CDI bootstrap.
 * Application code can also register handlers at runtime:</p>
 *
 * <pre>{@code
 * @Inject OjsJobRegistry registry;
 *
 * registry.register("report.generate", ctx -> {
 *     // handler logic
 *     return Map.of("ok", true);
 * });
 * }</pre>
 */
@ApplicationScoped
public class OjsJobRegistry {

    private final Map<String, JobHandler> handlers = new ConcurrentHashMap<>();

    /**
     * Register a handler for the given job type.
     *
     * @param jobType the OJS job type (e.g. "email.send")
     * @param handler the handler to invoke when jobs of this type are fetched
     * @throws IllegalArgumentException if jobType is null or blank
     */
    public void register(String jobType, JobHandler handler) {
        if (jobType == null || jobType.isBlank()) {
            throw new IllegalArgumentException("Job type must not be null or blank");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler must not be null");
        }
        handlers.put(jobType, handler);
    }

    /**
     * Returns an unmodifiable view of all registered handlers.
     */
    public Map<String, JobHandler> getHandlers() {
        return Collections.unmodifiableMap(handlers);
    }

    /**
     * Check whether a handler is registered for the given job type.
     */
    public boolean hasHandler(String jobType) {
        return handlers.containsKey(jobType);
    }

    /**
     * Returns the number of registered handlers.
     */
    public int size() {
        return handlers.size();
    }
}
