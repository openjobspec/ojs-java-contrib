package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.SSESubscription;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Bridges OJS server-sent events (SSE) to Spring's {@link ApplicationEventPublisher}.
 *
 * <p>Subscribes to OJS event streams and re-publishes them as {@link OjsJobEvent}
 * instances, enabling idiomatic Spring {@code @EventListener} usage:
 *
 * <pre>{@code
 * @Autowired OjsEventBridge events;
 *
 * // Subscribe to all events for a specific job
 * events.subscribeToJob("job-id-123");
 *
 * // Subscribe to all events on a queue
 * events.subscribeToQueue("default");
 *
 * // Listen in any Spring bean
 * @EventListener
 * public void onEvent(OjsJobEvent event) {
 *     log.info("Received {} for job {}", event.getEventType(), event.getJobId());
 * }
 * }</pre>
 */
public class OjsEventBridge {

    private final String serverUrl;
    private final ApplicationEventPublisher publisher;
    private final CopyOnWriteArrayList<SSESubscription> subscriptions = new CopyOnWriteArrayList<>();

    public OjsEventBridge(String serverUrl, ApplicationEventPublisher publisher) {
        this.serverUrl = Objects.requireNonNull(serverUrl, "serverUrl must not be null");
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
    }

    /**
     * Subscribe to SSE events for a specific job and publish them as Spring events.
     *
     * @param jobId the job ID to subscribe to
     * @return the SSE subscription (can be cancelled)
     */
    public SSESubscription subscribeToJob(String jobId) {
        var subscription = SSESubscription.subscribeJob(
                serverUrl, jobId, event -> publishEvent(event, jobId));
        subscriptions.add(subscription);
        return subscription;
    }

    /**
     * Subscribe to SSE events for a queue and publish them as Spring events.
     *
     * @param queue the queue name to subscribe to
     * @return the SSE subscription (can be cancelled)
     */
    public SSESubscription subscribeToQueue(String queue) {
        var subscription = SSESubscription.subscribeQueue(
                serverUrl, queue, event -> publishEvent(event, null));
        subscriptions.add(subscription);
        return subscription;
    }

    /**
     * Subscribe to SSE events on a custom channel and publish them as Spring events.
     *
     * @param channel the SSE channel path
     * @return the SSE subscription (can be cancelled)
     */
    public SSESubscription subscribe(String channel) {
        var subscription = SSESubscription.subscribe(
                serverUrl, channel, event -> publishEvent(event, null));
        subscriptions.add(subscription);
        return subscription;
    }

    /**
     * Cancel all active subscriptions. Called during shutdown to clean up resources.
     */
    public void cancelAll() {
        for (var sub : subscriptions) {
            try {
                sub.cancel();
            } catch (Exception ignored) {
                // Best-effort cleanup
            }
        }
        subscriptions.clear();
    }

    /** The number of active subscriptions. */
    public int activeSubscriptionCount() {
        return subscriptions.size();
    }

    private void publishEvent(SSESubscription.SSEEvent sseEvent, String jobId) {
        var data = sseEvent.data() != null
                ? Map.<String, Object>of("raw", sseEvent.data())
                : Map.<String, Object>of();
        var event = new OjsJobEvent(this, sseEvent.type(), jobId, data);
        publisher.publishEvent(event);
    }
}
