package org.openjobspec.spring;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OjsJobEventTest {

    @Test
    void constructionAndGetters() {
        var data = Map.<String, Object>of("status", "completed");
        var event = new OjsJobEvent(this, "job.completed", "job-123", data);

        assertEquals("job.completed", event.getEventType());
        assertEquals("job-123", event.getJobId());
        assertEquals(data, event.getData());
        assertSame(this, event.getSource());
    }

    @Test
    void nullJobIdAllowed() {
        var event = new OjsJobEvent(this, "worker.started", null, Map.of());
        assertNull(event.getJobId());
    }

    @Test
    void nullDataBecomesEmptyMap() {
        var event = new OjsJobEvent(this, "job.completed", "job-1", null);
        assertNotNull(event.getData());
        assertTrue(event.getData().isEmpty());
    }

    @Test
    void dataIsImmutableCopy() {
        var mutable = new java.util.HashMap<String, Object>();
        mutable.put("key", "val");
        var event = new OjsJobEvent(this, "job.completed", "job-1", mutable);

        mutable.put("extra", "should not appear");
        assertFalse(event.getData().containsKey("extra"));
        assertThrows(UnsupportedOperationException.class,
                () -> event.getData().put("new", "value"));
    }

    @Test
    void isTypeMatches() {
        var event = new OjsJobEvent(this, "job.completed", "job-1", Map.of());

        assertTrue(event.isType("job.completed"));
        assertFalse(event.isType("job.failed"));
    }

    @Test
    void isJobEvent() {
        assertTrue(new OjsJobEvent(this, "job.completed", "j", Map.of()).isJobEvent());
        assertTrue(new OjsJobEvent(this, "job.failed", "j", Map.of()).isJobEvent());
        assertFalse(new OjsJobEvent(this, "workflow.completed", "w", Map.of()).isJobEvent());
        assertFalse(new OjsJobEvent(this, "worker.started", null, Map.of()).isJobEvent());
    }

    @Test
    void isWorkflowEvent() {
        assertTrue(new OjsJobEvent(this, "workflow.completed", "w", Map.of()).isWorkflowEvent());
        assertTrue(new OjsJobEvent(this, "workflow.failed", "w", Map.of()).isWorkflowEvent());
        assertFalse(new OjsJobEvent(this, "job.completed", "j", Map.of()).isWorkflowEvent());
    }

    @Test
    void isWorkerEvent() {
        assertTrue(new OjsJobEvent(this, "worker.started", null, Map.of()).isWorkerEvent());
        assertTrue(new OjsJobEvent(this, "worker.stopped", null, Map.of()).isWorkerEvent());
        assertFalse(new OjsJobEvent(this, "job.completed", "j", Map.of()).isWorkerEvent());
    }

    @Test
    void rejectsNullEventType() {
        assertThrows(NullPointerException.class,
                () -> new OjsJobEvent(this, null, "job-1", Map.of()));
    }

    @Test
    void toStringIncludesFields() {
        var event = new OjsJobEvent(this, "job.completed", "job-42", Map.of("k", "v"));
        var str = event.toString();

        assertTrue(str.contains("job.completed"));
        assertTrue(str.contains("job-42"));
    }
}
