package org.openjobspec.micronaut;

import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.OJSClient;
import org.reactivestreams.Publisher;

import java.util.Map;

/**
 * Micronaut health indicator that checks the OJS backend connectivity.
 */
@Singleton
public class OjsHealthIndicator implements HealthIndicator {

    private final OJSClient client;

    public OjsHealthIndicator(OJSClient client) {
        this.client = client;
    }

    @Override
    public Publisher<HealthResult> getResult() {
        return subscriber -> {
            subscriber.onSubscribe(new org.reactivestreams.Subscription() {
                @Override
                public void request(long n) {
                    try {
                        var status = client.health();
                        subscriber.onNext(HealthResult.builder("ojs")
                                .status(HealthStatus.UP)
                                .details(status)
                                .build());
                    } catch (Exception e) {
                        subscriber.onNext(HealthResult.builder("ojs")
                                .status(HealthStatus.DOWN)
                                .details(Map.of("error", e.getMessage()))
                                .build());
                    }
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {}
            });
        };
    }
}
