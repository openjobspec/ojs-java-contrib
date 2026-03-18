package org.openjobspec.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Manages the OJS worker lifecycle in sync with the Micronaut server.
 *
 * <p>On {@link ServerStartupEvent}, registers any decryption middleware and starts
 * the worker on a virtual thread. On {@link ApplicationShutdownEvent}, gracefully
 * stops the worker.</p>
 *
 * <p>Disabled when {@code ojs.worker-enabled} is {@code false}.</p>
 */
@Singleton
@Requires(property = "ojs.worker-enabled", notEquals = "false")
public class OjsWorkerLifecycle implements ApplicationEventListener<ServerStartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(OjsWorkerLifecycle.class);

    private final OJSWorker worker;
    private final List<Middleware> middlewares;

    public OjsWorkerLifecycle(OJSWorker worker, List<Middleware> middlewares) {
        this.worker = worker;
        this.middlewares = middlewares != null ? middlewares : List.of();
    }

    /**
     * Registers middleware and starts the OJS worker when the server starts.
     * The worker runs on a virtual thread so the startup event is non-blocking.
     */
    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        for (Middleware mw : middlewares) {
            worker.use(mw);
        }
        LOG.info("Starting OJS worker");
        Thread.ofVirtual().name("ojs-worker").start(worker::start);
    }

    /**
     * Handles graceful shutdown of the OJS worker.
     * Called via Micronaut's {@code @EventListener} for {@link ApplicationShutdownEvent}.
     */
    @io.micronaut.runtime.event.annotation.EventListener
    void onShutdown(ApplicationShutdownEvent event) {
        LOG.info("Stopping OJS worker");
        worker.stop();
    }
}
