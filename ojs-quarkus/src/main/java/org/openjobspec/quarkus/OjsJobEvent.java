package org.openjobspec.quarkus;

import org.openjobspec.ojs.SSESubscription;

import java.time.Instant;

/**
 * CDI event fired by the {@link OjsEventBridge} whenever an OJS lifecycle
 * event arrives over the SSE channel.
 *
 * <p>Observe these events to react to job, workflow, worker, cron, or queue
 * state changes:</p>
 *
 * <pre>{@code
 * @ApplicationScoped
 * public class JobEventListener {
 *
 *     void onJobCompleted(@Observes OjsJobEvent event) {
 *         if (event.isJobEvent()) {
 *             System.out.println("Job event: " + event.eventType());
 *         }
 *     }
 * }
 * }</pre>
 *
 * @param id         the SSE event identifier (may be {@code null})
 * @param eventType  the event type, e.g. {@code "job.completed"} or
 *                   {@code "workflow.started"}
 * @param data       the raw JSON payload from the SSE stream
 * @param receivedAt the instant this event was received by the bridge
 */
public record OjsJobEvent(
        String id,
        String eventType,
        String data,
        Instant receivedAt
) {

    /** Create an {@code OjsJobEvent} from a raw SSE event. */
    public static OjsJobEvent from(SSESubscription.SSEEvent sseEvent) {
        return new OjsJobEvent(
                sseEvent.id(),
                sseEvent.type(),
                sseEvent.data(),
                Instant.now()
        );
    }

    /** {@code true} if this is a job lifecycle event (type starts with {@code "job."}). */
    public boolean isJobEvent() {
        return eventType != null && eventType.startsWith("job.");
    }

    /** {@code true} if this is a worker event (type starts with {@code "worker."}). */
    public boolean isWorkerEvent() {
        return eventType != null && eventType.startsWith("worker.");
    }

    /** {@code true} if this is a workflow event (type starts with {@code "workflow."}). */
    public boolean isWorkflowEvent() {
        return eventType != null && eventType.startsWith("workflow.");
    }

    /** {@code true} if this is a cron event (type starts with {@code "cron."}). */
    public boolean isCronEvent() {
        return eventType != null && eventType.startsWith("cron.");
    }

    /** {@code true} if this is a queue event (type starts with {@code "queue."}). */
    public boolean isQueueEvent() {
        return eventType != null && eventType.startsWith("queue.");
    }
}
