package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.OJSClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsCronBridgeTest {

    @Mock
    OJSClient client;

    @Test
    void registerDelegatesToClient() {
        var cronJob = Map.<String, Object>of(
                "name", "daily-report",
                "cron", "0 8 * * *",
                "type", "report.generate");
        var expected = Map.<String, Object>of("name", "daily-report", "status", "registered");
        when(client.registerCronJob(cronJob)).thenReturn(expected);

        var bridge = new OjsCronBridge(client);
        var result = bridge.register(cronJob);

        assertEquals(expected, result);
        verify(client).registerCronJob(cronJob);
    }

    @Test
    void unregisterDelegatesToClient() {
        var bridge = new OjsCronBridge(client);
        bridge.unregister("daily-report");
        verify(client).unregisterCronJob("daily-report");
    }

    @Test
    void listDelegatesToClient() {
        var expected = List.<Map<String, Object>>of(
                Map.of("name", "job-1"),
                Map.of("name", "job-2"));
        when(client.listCronJobs()).thenReturn(expected);

        var bridge = new OjsCronBridge(client);
        var result = bridge.list();

        assertEquals(expected, result);
        verify(client).listCronJobs();
    }

    @SuppressWarnings("unchecked")
    @Test
    void syncFromPropertiesRegistersAllDefinitions() {
        var def1 = new OjsProperties.CronDefinition();
        def1.setName("daily-report");
        def1.setCron("0 8 * * *");
        def1.setType("report.generate");
        def1.setQueue("reports");
        def1.setArgs(Map.of("scope", "daily"));

        var def2 = new OjsProperties.CronDefinition();
        def2.setName("hourly-cleanup");
        def2.setCron("0 * * * *");
        def2.setType("cleanup.run");

        when(client.registerCronJob(any(Map.class))).thenReturn(Map.of());

        var bridge = new OjsCronBridge(client);
        bridge.syncFromProperties(List.of(def1, def2));

        var captor = ArgumentCaptor.forClass(Map.class);
        verify(client, times(2)).registerCronJob(captor.capture());

        var registered = captor.getAllValues();
        assertEquals("daily-report", registered.get(0).get("name"));
        assertEquals("0 8 * * *", registered.get(0).get("cron"));
        assertEquals("report.generate", registered.get(0).get("type"));
        assertEquals("reports", registered.get(0).get("queue"));

        assertEquals("hourly-cleanup", registered.get(1).get("name"));
        assertEquals("0 * * * *", registered.get(1).get("cron"));
    }

    @Test
    void syncFromPropertiesHandlesNullList() {
        var bridge = new OjsCronBridge(client);
        bridge.syncFromProperties(null);
        verify(client, never()).registerCronJob(any());
    }

    @Test
    void syncFromPropertiesHandlesEmptyList() {
        var bridge = new OjsCronBridge(client);
        bridge.syncFromProperties(List.of());
        verify(client, never()).registerCronJob(any());
    }

    @Test
    void constructorRejectsNullClient() {
        assertThrows(NullPointerException.class, () -> new OjsCronBridge(null));
    }
}
