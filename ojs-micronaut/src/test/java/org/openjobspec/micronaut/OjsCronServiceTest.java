package org.openjobspec.micronaut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.OJSClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsCronServiceTest {

    @Mock OJSClient client;

    @Test
    void isSingleton() {
        assertTrue(OjsCronService.class.isAnnotationPresent(
                jakarta.inject.Singleton.class));
    }

    @Test
    void listDelegatesToClient() {
        var expected = List.of(Map.<String, Object>of("name", "cleanup"));
        when(client.listCronJobs()).thenReturn(expected);

        var service = new OjsCronService(client);
        var result = service.list();

        assertEquals(expected, result);
        verify(client).listCronJobs();
    }

    @Test
    void registerDelegatesToClient() {
        var response = Map.<String, Object>of("name", "daily-report", "schedule", "0 0 * * *");
        when(client.registerCronJob(any())).thenReturn(response);

        var service = new OjsCronService(client);
        var result = service.register("daily-report", "0 0 * * *",
                "report.generate", Map.of("format", "pdf"));

        assertEquals(response, result);
        verify(client).registerCronJob(argThat(map ->
                "daily-report".equals(map.get("name"))
                        && "0 0 * * *".equals(map.get("schedule"))
                        && "report.generate".equals(map.get("type"))
        ));
    }

    @Test
    void registerWithQueueDelegatesToClient() {
        var response = Map.<String, Object>of("name", "hourly-sync");
        when(client.registerCronJob(any())).thenReturn(response);

        var service = new OjsCronService(client);
        var result = service.register("hourly-sync", "0 * * * *",
                "sync.run", Map.of(), "background");

        assertEquals(response, result);
        verify(client).registerCronJob(argThat(map ->
                "background".equals(map.get("queue"))
        ));
    }

    @Test
    void unregisterDelegatesToClient() {
        var service = new OjsCronService(client);
        service.unregister("old-job");

        verify(client).unregisterCronJob("old-job");
    }

    @Test
    void registerPassesArgsCorrectly() {
        when(client.registerCronJob(any())).thenReturn(Map.of());

        var service = new OjsCronService(client);
        var args = Map.<String, Object>of("key1", "val1", "key2", 42);
        service.register("test", "* * * * *", "test.job", args);

        verify(client).registerCronJob(argThat(map -> {
            @SuppressWarnings("unchecked")
            var passedArgs = (Map<String, Object>) map.get("args");
            return passedArgs.equals(args);
        }));
    }
}
