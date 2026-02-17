package org.openjobspec.micronaut;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;

/**
 * Micronaut factory producing OJS client and worker beans.
 */
@Factory
public class OjsFactory {

    @Singleton
    public OJSClient ojsClient(OjsConfiguration config) {
        return OJSClient.builder()
                .url(config.getUrl())
                .build();
    }

    @Singleton
    public OJSWorker ojsWorker(OjsConfiguration config) {
        return OJSWorker.builder()
                .url(config.getUrl())
                .queues(config.getQueues())
                .concurrency(config.getConcurrency())
                .build();
    }
}
