package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsHealthIndicatorTest {

    @Mock
    OJSClient client;

    @Mock
    OJSWorker worker;

    @Test
    void reportsUpWhenServerHealthy() {
        when(client.health()).thenReturn(Map.of("status", "ok"));
        when(client.listQueues()).thenReturn(List.of());

        var indicator = new OjsHealthIndicator(client);
        var health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("ok", health.getDetails().get("status"));
    }

    @Test
    void reportsDownWhenServerUnhealthy() {
        when(client.health()).thenThrow(new RuntimeException("Connection refused"));

        var indicator = new OjsHealthIndicator(client);
        var health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
    }

    @Test
    void includesWorkerDetailsWhenAvailable() {
        when(client.health()).thenReturn(Map.of("status", "ok"));
        when(client.listQueues()).thenReturn(List.of());
        when(worker.getWorkerId()).thenReturn("worker_abc123");
        when(worker.getState()).thenReturn(OJSWorker.State.RUNNING);
        when(worker.getActiveJobCount()).thenReturn(5);

        var indicator = new OjsHealthIndicator(client, worker);
        var health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        @SuppressWarnings("unchecked")
        var workerDetails = (Map<String, Object>) health.getDetails().get("worker");
        assertNotNull(workerDetails);
        assertEquals("worker_abc123", workerDetails.get("id"));
        assertEquals("running", workerDetails.get("state"));
        assertEquals(5, workerDetails.get("activeJobs"));
    }

    @Test
    void workerStateIncludedOnDown() {
        when(client.health()).thenThrow(new RuntimeException("Connection refused"));
        when(worker.getState()).thenReturn(OJSWorker.State.RUNNING);

        var indicator = new OjsHealthIndicator(client, worker);
        var health = indicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertEquals("running", health.getDetails().get("worker.state"));
    }

    @Test
    void worksWithoutWorker() {
        when(client.health()).thenReturn(Map.of("status", "ok"));
        when(client.listQueues()).thenReturn(List.of());

        var indicator = new OjsHealthIndicator(client, null);
        var health = indicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertNull(health.getDetails().get("worker"));
    }
}
