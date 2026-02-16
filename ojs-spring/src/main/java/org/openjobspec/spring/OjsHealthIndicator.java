package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Spring Actuator health indicator that checks the OJS backend via the
 * {@code /health} endpoint.
 */
public class OjsHealthIndicator implements HealthIndicator {

    private final OJSClient client;

    public OjsHealthIndicator(OJSClient client) {
        this.client = client;
    }

    @Override
    public Health health() {
        try {
            var status = client.health();
            return Health.up()
                    .withDetails(status)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withException(e)
                    .build();
        }
    }
}
