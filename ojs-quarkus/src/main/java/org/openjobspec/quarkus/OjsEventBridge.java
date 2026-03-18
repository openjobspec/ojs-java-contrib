package org.openjobspec.quarkus;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.openjobspec.ojs.SSESubscription;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bridges OJS Server-Sent Events into the CDI event bus.
 *
 * <p>When {@code ojs.events.enabled=true}, this bean subscribes to the
 * configured SSE channel on application startup and fires {@link OjsJobEvent}
 * instances that any CDI observer can consume:</p>
 *
 * <pre>{@code
 * void onJob(@Observes OjsJobEvent e) {
 *     if (e.isJobEvent()) { log.info("Job event: " + e.eventType()); }
 * }
 * }</pre>
 */
@ApplicationScoped
public class OjsEventBridge {

    private static final Logger LOG = Logger.getLogger(OjsEventBridge.class.getName());

    @Inject
    OjsConfig config;

    @Inject
    Event<OjsJobEvent> cdiEvent;

    private volatile SSESubscription subscription;

    /**
     * Subscribe to the SSE channel when the application starts (if enabled).
     */
    void onStart(@Observes StartupEvent ev) {
        if (!config.events().enabled()) {
            LOG.fine("OJS event bridge is disabled");
            return;
        }

        var channel = config.events().channel();
        LOG.info(() -> "Subscribing to OJS events on channel: " + channel);

        try {
            subscription = SSESubscription.subscribe(
                    config.url(),
                    channel,
                    sseEvent -> {
                        try {
                            cdiEvent.fire(OjsJobEvent.from(sseEvent));
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, "Failed to fire CDI event", e);
                        }
                    }
            );
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to subscribe to OJS events", e);
        }
    }

    /**
     * Cancel the SSE subscription when the application shuts down.
     */
    void onStop(@Observes ShutdownEvent ev) {
        if (subscription != null) {
            LOG.info("Closing OJS event subscription");
            try {
                subscription.close();
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Error closing SSE subscription", e);
            }
            subscription = null;
        }
    }

    /** Returns {@code true} if the event bridge is actively subscribed. */
    public boolean isSubscribed() {
        return subscription != null;
    }
}
