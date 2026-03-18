package org.openjobspec.micronaut;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.Event;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OjsJobEventTest {

    @Test
    void wrapsOjsEvent() {
        var event = Event.of("job.completed", "job-123", Map.of("result", "ok"));
        var jobEvent = new OjsJobEvent(event);

        assertSame(event, jobEvent.event());
    }

    @Test
    void delegatesType() {
        var event = Event.of("job.completed", "job-123", Map.of());
        var jobEvent = new OjsJobEvent(event);

        assertEquals("job.completed", jobEvent.type());
    }

    @Test
    void delegatesSubject() {
        var event = Event.of("job.started", "job-456", Map.of());
        var jobEvent = new OjsJobEvent(event);

        assertEquals("job-456", jobEvent.subject());
    }

    @Test
    void delegatesData() {
        var data = Map.<String, Object>of("key", "value");
        var event = Event.of("job.completed", "job-789", data);
        var jobEvent = new OjsJobEvent(event);

        assertEquals(data, jobEvent.data());
    }

    @Test
    void isJobEventDelegates() {
        var event = Event.of("job.completed", "job-1", Map.of());
        var jobEvent = new OjsJobEvent(event);

        assertEquals(event.isJobEvent(), jobEvent.isJobEvent());
    }

    @Test
    void isWorkflowEventDelegates() {
        var event = Event.of("workflow.completed", "wf-1", Map.of());
        var jobEvent = new OjsJobEvent(event);

        assertEquals(event.isWorkflowEvent(), jobEvent.isWorkflowEvent());
    }

    @Test
    void isWorkerEventDelegates() {
        var event = Event.of("worker.started", "w-1", Map.of());
        var jobEvent = new OjsJobEvent(event);

        assertEquals(event.isWorkerEvent(), jobEvent.isWorkerEvent());
    }

    @Test
    void isRecord() {
        assertTrue(OjsJobEvent.class.isRecord());
    }

    @Test
    void recordEquality() {
        var event = Event.of("job.completed", "job-1", Map.of());
        var a = new OjsJobEvent(event);
        var b = new OjsJobEvent(event);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
