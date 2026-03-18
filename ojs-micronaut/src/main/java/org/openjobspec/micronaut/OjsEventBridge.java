package org.openjobspec.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.Event;
import org.openjobspec.ojs.SSESubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bridges OJS server-sent events (SSE) into the Micronaut event system.
 *
 * <p>When enabled via {@code ojs.events-enabled=true}, subscribes to the OJS SSE
 * stream on server startup and publishes each event as an {@link OjsJobEvent}
 * through Micronaut's {@link ApplicationEventPublisher}.</p>
 *
 * <p>The subscription is cancelled on application shutdown.</p>
 */
@Singleton
@Requires(property = "ojs.events-enabled", value = "true")
public class OjsEventBridge implements ApplicationEventListener<ServerStartupEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(OjsEventBridge.class);

    private final OjsConfiguration config;
    private final ApplicationEventPublisher<OjsJobEvent> publisher;
    private volatile SSESubscription subscription;

    public OjsEventBridge(OjsConfiguration config,
                          ApplicationEventPublisher<OjsJobEvent> publisher) {
        this.config = config;
        this.publisher = publisher;
    }

    /**
     * Subscribes to the OJS SSE event stream when the server starts.
     */
    @Override
    public void onApplicationEvent(ServerStartupEvent event) {
        String channel = config.getEventsChannel();
        LOG.info("Subscribing to OJS events on channel '{}'", channel);
        this.subscription = SSESubscription.subscribe(
                config.getUrl(),
                channel,
                sseEvent -> {
                    Event ojsEvent = Event.fromMap(
                            org.openjobspec.ojs.transport.Json.decodeObject(sseEvent.data())
                    );
                    publisher.publishEvent(new OjsJobEvent(ojsEvent));
                }
        );
    }

    /**
     * Cancels the SSE subscription on application shutdown.
     */
    @io.micronaut.runtime.event.annotation.EventListener
    void onShutdown(ApplicationShutdownEvent event) {
        if (subscription != null) {
            LOG.info("Cancelling OJS event subscription");
            subscription.cancel();
        }
    }

    /** Returns the current SSE subscription, or {@code null} if not started. */
    public SSESubscription getSubscription() {
        return subscription;
    }
}
