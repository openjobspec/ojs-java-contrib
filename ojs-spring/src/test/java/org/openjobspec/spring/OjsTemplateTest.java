package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.Job;
import org.openjobspec.ojs.OJSClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsTemplateTest {

    @Mock
    OJSClient client;

    @Test
    void enqueueDelegatesToClient() {
        var template = new OjsTemplate(client, "default");
        var expectedJob = createTestJob("test-id");
        when(client.enqueue("email.send", Map.of("to", "user@test.com"))).thenReturn(expectedJob);

        var result = template.enqueue("email.send", Map.of("to", "user@test.com"));

        assertEquals(expectedJob, result);
        verify(client).enqueue("email.send", Map.of("to", "user@test.com"));
    }

    @Test
    void getJobDelegatesToClient() {
        var template = new OjsTemplate(client, "default");
        var expectedJob = createTestJob("job-123");
        when(client.getJob("job-123")).thenReturn(expectedJob);

        var result = template.getJob("job-123");

        assertEquals(expectedJob, result);
        verify(client).getJob("job-123");
    }

    @Test
    void cancelJobDelegatesToClient() {
        var template = new OjsTemplate(client, "default");
        var expectedJob = createTestJob("job-456");
        when(client.cancelJob("job-456")).thenReturn(expectedJob);

        var result = template.cancelJob("job-456");

        assertEquals(expectedJob, result);
        verify(client).cancelJob("job-456");
    }

    @Test
    void healthDelegatesToClient() {
        var template = new OjsTemplate(client, "default");
        var expectedHealth = Map.<String, Object>of("status", "ok");
        when(client.health()).thenReturn(expectedHealth);

        var result = template.health();

        assertEquals(expectedHealth, result);
    }

    @Test
    void getClientReturnsUnderlying() {
        var template = new OjsTemplate(client, "default");
        assertSame(client, template.getClient());
    }

    private static Job createTestJob(String id) {
        return new Job(Job.SPEC_VERSION, id, "test.job", "default",
                List.of(), Map.of(), 0, 0, null, null, null, null, null,
                "available", 0, null, null, null, null, null, null, List.of());
    }
}
