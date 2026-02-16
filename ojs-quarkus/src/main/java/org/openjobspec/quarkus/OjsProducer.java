package org.openjobspec.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;

/**
 * CDI producer for OJS client and worker beans.
 */
@ApplicationScoped
public class OjsProducer {

    @Produces
    @Singleton
    public OJSClient ojsClient(OjsConfig config) {
        return OJSClient.builder()
                .url(config.url())
                .build();
    }

    @Produces
    @Singleton
    public OJSWorker ojsWorker(OjsConfig config) {
        return OJSWorker.builder()
                .url(config.url())
                .queues(config.queues())
                .concurrency(config.concurrency())
                .build();
    }
}
