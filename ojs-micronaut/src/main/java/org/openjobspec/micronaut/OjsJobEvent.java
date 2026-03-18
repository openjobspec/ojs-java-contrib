package org.openjobspec.micronaut;

import org.openjobspec.ojs.Event;

import java.util.Map;

/**
 * Micronaut application event wrapping an OJS lifecycle {@link Event}.
 *
 * <p>Published by {@link OjsEventBridge} whenever the OJS server emits an SSE event.
 * Application beans can listen for these events using Micronaut's {@code @EventListener}:</p>
 *
 * <pre>{@code
 * @Singleton
 * public class JobLogger {
 *     @EventListener
 *     void onJobEvent(OjsJobEvent event) {
 *         System.out.println("OJS event: " + event.type());
 *     }
 * }
 * }</pre>
 *
 * @param event the underlying OJS event
 */
public record OjsJobEvent(Event event) {

    /** Returns the event type (e.g. {@code "job.completed"}). */
    public String type() {
        return event.type();
    }

    /** Returns the event subject (typically a job or workflow ID). */
    public String subject() {
        return event.subject();
    }

    /** Returns the event data payload. */
    public Map<String, Object> data() {
        return event.data();
    }

    /** Returns {@code true} if this is a job lifecycle event. */
    public boolean isJobEvent() {
        return event.isJobEvent();
    }

    /** Returns {@code true} if this is a workflow lifecycle event. */
    public boolean isWorkflowEvent() {
        return event.isWorkflowEvent();
    }

    /** Returns {@code true} if this is a worker lifecycle event. */
    public boolean isWorkerEvent() {
        return event.isWorkerEvent();
    }
}
