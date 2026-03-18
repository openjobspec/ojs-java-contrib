package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OjsJobEventTest {

    @Test
    void isRecord() {
        assertTrue(OjsJobEvent.class.isRecord());
    }

    @Test
    void recordComponentsAreAccessible() {
        var now = Instant.now();
        var event = new OjsJobEvent("evt-1", "job.completed", "{\"id\":\"abc\"}", now);

        assertEquals("evt-1", event.id());
        assertEquals("job.completed", event.eventType());
        assertEquals("{\"id\":\"abc\"}", event.data());
        assertEquals(now, event.receivedAt());
    }

    @Test
    void isJobEventDetectsJobTypes() {
        assertTrue(new OjsJobEvent(null, "job.completed", null, Instant.now()).isJobEvent());
        assertTrue(new OjsJobEvent(null, "job.enqueued", null, Instant.now()).isJobEvent());
        assertTrue(new OjsJobEvent(null, "job.failed", null, Instant.now()).isJobEvent());
        assertFalse(new OjsJobEvent(null, "worker.started", null, Instant.now()).isJobEvent());
        assertFalse(new OjsJobEvent(null, null, null, Instant.now()).isJobEvent());
    }

    @Test
    void isWorkerEventDetectsWorkerTypes() {
        assertTrue(new OjsJobEvent(null, "worker.started", null, Instant.now()).isWorkerEvent());
        assertTrue(new OjsJobEvent(null, "worker.stopped", null, Instant.now()).isWorkerEvent());
        assertFalse(new OjsJobEvent(null, "job.completed", null, Instant.now()).isWorkerEvent());
    }

    @Test
    void isWorkflowEventDetectsWorkflowTypes() {
        assertTrue(new OjsJobEvent(null, "workflow.started", null, Instant.now()).isWorkflowEvent());
        assertTrue(new OjsJobEvent(null, "workflow.completed", null, Instant.now()).isWorkflowEvent());
        assertFalse(new OjsJobEvent(null, "job.completed", null, Instant.now()).isWorkflowEvent());
    }

    @Test
    void isCronEventDetectsCronTypes() {
        assertTrue(new OjsJobEvent(null, "cron.triggered", null, Instant.now()).isCronEvent());
        assertFalse(new OjsJobEvent(null, "job.completed", null, Instant.now()).isCronEvent());
    }

    @Test
    void isQueueEventDetectsQueueTypes() {
        assertTrue(new OjsJobEvent(null, "queue.paused", null, Instant.now()).isQueueEvent());
        assertTrue(new OjsJobEvent(null, "queue.resumed", null, Instant.now()).isQueueEvent());
        assertFalse(new OjsJobEvent(null, "job.completed", null, Instant.now()).isQueueEvent());
    }

    @Test
    void nullEventTypeReturnsFalseForAllChecks() {
        var event = new OjsJobEvent(null, null, null, Instant.now());
        assertFalse(event.isJobEvent());
        assertFalse(event.isWorkerEvent());
        assertFalse(event.isWorkflowEvent());
        assertFalse(event.isCronEvent());
        assertFalse(event.isQueueEvent());
    }

    @Test
    void equalsAndHashCode() {
        var now = Instant.now();
        var a = new OjsJobEvent("1", "job.done", "{}", now);
        var b = new OjsJobEvent("1", "job.done", "{}", now);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringIncludesFields() {
        var event = new OjsJobEvent("e1", "job.completed", "{}", Instant.EPOCH);
        var str = event.toString();

        assertTrue(str.contains("e1"));
        assertTrue(str.contains("job.completed"));
    }
}
