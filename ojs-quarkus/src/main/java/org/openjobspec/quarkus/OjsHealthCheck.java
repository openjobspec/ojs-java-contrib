package org.openjobspec.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.openjobspec.ojs.OJSClient;

/**
 * MicroProfile Health readiness check for the OJS backend.
 */
@Readiness
@ApplicationScoped
public class OjsHealthCheck implements HealthCheck {

    @Inject
    OJSClient client;

    @Override
    public HealthCheckResponse call() {
        try {
            var status = client.health();
            return HealthCheckResponse.named("ojs")
                    .up()
                    .withData("status", String.valueOf(status))
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("ojs")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}
