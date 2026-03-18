package org.openjobspec.spring;

import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.Objects;

/**
 * Spring {@link ApplicationEvent} wrapping an OJS lifecycle event.
 *
 * <p>Published by {@link OjsEventBridge} when server-sent events are received.
 * Listen for these events using {@code @EventListener}:
 *
 * <pre>{@code
 * @EventListener
 * public void onJobCompleted(OjsJobEvent event) {
 *     if (event.isType("job.completed")) {
 *         log.info("Job {} completed", event.getJobId());
 *     }
 * }
 *
 * @EventListener(condition = "#event.eventType == 'job.failed'")
 * public void onJobFailed(OjsJobEvent event) {
 *     alertService.notify("Job failed: " + event.getJobId());
 * }
 * }</pre>
 */
public class OjsJobEvent extends ApplicationEvent {

    private final String eventType;
    private final String jobId;
    private final Map<String, Object> data;

    /**
     * Create a new OJS job event.
     *
     * @param source    the event source (typically the OjsEventBridge)
     * @param eventType the OJS event type (e.g. "job.completed", "job.failed")
     * @param jobId     the job ID (may be null for non-job events)
     * @param data      the event payload
     */
    public OjsJobEvent(Object source, String eventType, String jobId, Map<String, Object> data) {
        super(source);
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.jobId = jobId;
        this.data = data != null ? Map.copyOf(data) : Map.of();
    }

    /** The OJS event type (e.g. "job.completed", "job.failed", "workflow.completed"). */
    public String getEventType() {
        return eventType;
    }

    /** The job ID associated with this event, or {@code null} for non-job events. */
    public String getJobId() {
        return jobId;
    }

    /** The event payload data. */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Check if this event matches a specific type.
     *
     * @param type the event type to check (e.g. "job.completed")
     * @return true if this event is of the given type
     */
    public boolean isType(String type) {
        return eventType.equals(type);
    }

    /** Whether this is a job lifecycle event. */
    public boolean isJobEvent() {
        return eventType.startsWith("job.");
    }

    /** Whether this is a workflow lifecycle event. */
    public boolean isWorkflowEvent() {
        return eventType.startsWith("workflow.");
    }

    /** Whether this is a worker lifecycle event. */
    public boolean isWorkerEvent() {
        return eventType.startsWith("worker.");
    }

    @Override
    public String toString() {
        return "OjsJobEvent{type='%s', jobId='%s', data=%s}".formatted(eventType, jobId, data);
    }
}
