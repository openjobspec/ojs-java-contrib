package org.openjobspec.micronaut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.Workflow;
import org.openjobspec.ojs.Workflow.WorkflowStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsWorkflowServiceTest {

    @Mock OJSClient client;

    @Test
    void isSingleton() {
        assertTrue(OjsWorkflowService.class.isAnnotationPresent(
                jakarta.inject.Singleton.class));
    }

    @Test
    void stepCreatesWorkflowStep() {
        var service = new OjsWorkflowService(client);
        var step = service.step("email.send", Map.of("to", "user@test.com"));

        assertEquals("email.send", step.type());
        assertEquals("user@test.com", step.args().get("to"));
    }

    @Test
    void stepWithQueueCreatesWorkflowStep() {
        var service = new OjsWorkflowService(client);
        var step = service.step("email.send", Map.of(), "priority");

        assertEquals("email.send", step.type());
        assertEquals("priority", step.queue());
    }

    @Test
    void submitChainCallsClient() {
        var status = mock(WorkflowStatus.class);
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(status);

        var service = new OjsWorkflowService(client);
        var result = service.submitChain("test-chain",
                service.step("step.one", Map.of()),
                service.step("step.two", Map.of())
        );

        assertSame(status, result);
        verify(client).createWorkflow(argThat(def ->
                "chain".equals(def.type()) && "test-chain".equals(def.name())
        ));
    }

    @Test
    void submitGroupCallsClient() {
        var status = mock(WorkflowStatus.class);
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(status);

        var service = new OjsWorkflowService(client);
        var result = service.submitGroup("test-group",
                service.step("fan.a", Map.of()),
                service.step("fan.b", Map.of())
        );

        assertSame(status, result);
        verify(client).createWorkflow(argThat(def ->
                "group".equals(def.type()) && "test-group".equals(def.name())
        ));
    }

    @Test
    void submitBatchCallsClient() {
        var status = mock(WorkflowStatus.class);
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(status);

        var service = new OjsWorkflowService(client);
        var callbacks = service.callbacks()
                .onComplete(service.step("batch.done", Map.of()));
        var result = service.submitBatch("test-batch", callbacks,
                service.step("item.process", Map.of("id", 1))
        );

        assertSame(status, result);
        verify(client).createWorkflow(argThat(def ->
                "batch".equals(def.type()) && "test-batch".equals(def.name())
        ));
    }

    @Test
    void getWorkflowDelegatesToClient() {
        var status = mock(WorkflowStatus.class);
        when(client.getWorkflow("wf-123")).thenReturn(status);

        var service = new OjsWorkflowService(client);
        var result = service.getWorkflow("wf-123");

        assertSame(status, result);
        verify(client).getWorkflow("wf-123");
    }

    @Test
    void cancelWorkflowDelegatesToClient() {
        var service = new OjsWorkflowService(client);
        service.cancelWorkflow("wf-456");

        verify(client).cancelWorkflow("wf-456");
    }

    @Test
    void callbacksReturnsBuilder() {
        var service = new OjsWorkflowService(client);
        var builder = service.callbacks();
        assertNotNull(builder);
    }
}
