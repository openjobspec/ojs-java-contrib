package org.openjobspec.quarkus;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSWorker;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the {@link OJSWorker} lifecycle in alignment with the Quarkus
 * application lifecycle.
 *
 * <p>On startup this bean:</p>
 * <ol>
 *   <li>Registers any handlers found in the {@link OjsJobRegistry}.</li>
 *   <li>Installs all CDI-managed {@link Middleware} beans on the worker.</li>
 *   <li>Calls {@link OJSWorker#start()} (in a virtual thread so it does not
 *       block the Quarkus startup sequence).</li>
 * </ol>
 *
 * <p>On shutdown the worker is stopped gracefully, honouring the configured
 * grace period.</p>
 */
@ApplicationScoped
public class OjsWorkerLifecycle {

    private static final Logger LOG = Logger.getLogger(OjsWorkerLifecycle.class.getName());

    @Inject
    OJSWorker worker;

    @Inject
    OjsJobRegistry registry;

    @Inject
    OjsConfig config;

    @Inject
    @Any
    Instance<Middleware> middlewareInstances;

    private volatile Thread workerThread;

    /**
     * Start the worker when the Quarkus application starts.
     */
    void onStart(@Observes StartupEvent ev) {
        if (!config.worker().autoStart()) {
            LOG.info("OJS worker auto-start is disabled");
            return;
        }

        // Register handlers collected by the registry
        registry.getHandlers().forEach((type, handler) -> {
            worker.register(type, handler);
            LOG.fine(() -> "Registered OJS handler: " + type);
        });

        // Install CDI-managed middleware
        for (Middleware mw : middlewareInstances) {
            worker.use(mw);
        }

        // Start worker on a virtual thread so it doesn't block startup
        workerThread = Thread.ofVirtual()
                .name("ojs-worker")
                .start(() -> {
                    try {
                        LOG.info("Starting OJS worker");
                        worker.start();
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "OJS worker failed", e);
                    }
                });
    }

    /**
     * Stop the worker when the Quarkus application shuts down.
     */
    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("Stopping OJS worker");
        try {
            worker.stop();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Error stopping OJS worker", e);
        }
    }

    /** Returns {@code true} if the worker thread is alive. */
    public boolean isRunning() {
        return workerThread != null && workerThread.isAlive();
    }
}
