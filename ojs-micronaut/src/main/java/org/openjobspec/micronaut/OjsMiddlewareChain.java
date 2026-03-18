package org.openjobspec.micronaut;

import jakarta.inject.Singleton;
import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Micronaut-aware middleware chain for the OJS worker.
 *
 * <p>Collects all {@link Middleware} beans from the application context and
 * provides methods to inspect and programmatically add middleware. Middleware
 * registered here is automatically applied to the worker by
 * {@link OjsWorkerLifecycle} at startup.</p>
 *
 * <pre>{@code
 * @Inject OjsMiddlewareChain chain;
 *
 * chain.add(LoggingMiddleware.create());
 * chain.add("metrics", MetricsMiddleware.create());
 * }</pre>
 */
@Singleton
public class OjsMiddlewareChain {

    private static final Logger LOG = LoggerFactory.getLogger(OjsMiddlewareChain.class);

    private final List<Middleware> middlewares;

    public OjsMiddlewareChain(List<Middleware> middlewares) {
        this.middlewares = new ArrayList<>(middlewares != null ? middlewares : List.of());
    }

    /** Returns an unmodifiable view of the registered middleware list. */
    public List<Middleware> getMiddlewares() {
        return Collections.unmodifiableList(middlewares);
    }

    /** Adds a middleware to the end of the chain. */
    public void add(Middleware middleware) {
        middlewares.add(middleware);
        LOG.debug("Added middleware to OJS chain (total: {})", middlewares.size());
    }

    /** Adds a named middleware to the end of the chain. */
    public void add(String name, Middleware middleware) {
        middlewares.add(middleware);
        LOG.debug("Added middleware '{}' to OJS chain (total: {})", name, middlewares.size());
    }

    /** Returns the number of middleware in the chain. */
    public int size() {
        return middlewares.size();
    }

    /**
     * Applies all middleware in this chain to the given worker.
     * Called by {@link OjsWorkerLifecycle} during startup.
     */
    public void applyTo(OJSWorker worker) {
        for (Middleware mw : middlewares) {
            worker.use(mw);
        }
        LOG.info("Applied {} middleware(s) to OJS worker", middlewares.size());
    }
}
