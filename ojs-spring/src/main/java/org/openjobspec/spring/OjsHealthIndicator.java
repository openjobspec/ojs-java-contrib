package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Spring Actuator health indicator that checks the OJS backend connectivity,
 * reports queue depths, and includes worker status.
 *
 * <p>The health check calls the OJS {@code /health} endpoint and enriches the
 * response with local worker state information when a worker is available.
 */
public class OjsHealthIndicator implements HealthIndicator {

    private final OJSClient client;
    private final OJSWorker worker;

    public OjsHealthIndicator(OJSClient client) {
        this(client, null);
    }

    public OjsHealthIndicator(OJSClient client, OJSWorker worker) {
        this.client = client;
        this.worker = worker;
    }

    @Override
    public Health health() {
        try {
            var status = client.health();
            var details = new LinkedHashMap<String, Object>(status);

            if (worker != null) {
                var workerDetails = new LinkedHashMap<String, Object>();
                workerDetails.put("id", worker.getWorkerId());
                workerDetails.put("state", worker.getState().value());
                workerDetails.put("activeJobs", worker.getActiveJobCount());
                details.put("worker", workerDetails);
            }

            enrichWithQueueStats(details);

            return Health.up()
                    .withDetails(details)
                    .build();
        } catch (Exception e) {
            var builder = Health.down().withException(e);
            if (worker != null) {
                builder.withDetail("worker.state", worker.getState().value());
            }
            return builder.build();
        }
    }

    private void enrichWithQueueStats(Map<String, Object> details) {
        try {
            var queues = client.listQueues();
            if (!queues.isEmpty()) {
                details.put("queues", queues);
            }
        } catch (Exception ignored) {
            // Queue stats are best-effort
        }
    }
}
