package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.Workflow;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsWorkflowTemplateTest {

    @Mock
    OJSClient client;

    @Test
    void chainDelegatesToClient() {
        var expected = createWorkflowStatus("wf-1", "chain");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.chain("order-pipeline",
                Workflow.step("order.validate", Map.of("id", 1)),
                Workflow.step("order.charge", Map.of("id", 1)));

        assertSame(expected, result);
        verify(client).createWorkflow(any(Workflow.Definition.class));
    }

    @Test
    void groupDelegatesToClient() {
        var expected = createWorkflowStatus("wf-2", "group");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.group("notifications",
                Workflow.step("notify.email", Map.of()),
                Workflow.step("notify.sms", Map.of()));

        assertSame(expected, result);
        verify(client).createWorkflow(any(Workflow.Definition.class));
    }

    @Test
    void batchDelegatesToClient() {
        var expected = createWorkflowStatus("wf-3", "batch");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.batch("image-resize",
                List.of(Workflow.step("image.resize", Map.of("size", "sm"))),
                Workflow.step("image.complete", Map.of()),
                null, null);

        assertSame(expected, result);
        verify(client).createWorkflow(any(Workflow.Definition.class));
    }

    @Test
    void batchWithAllCallbacks() {
        var expected = createWorkflowStatus("wf-4", "batch");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.batch("full-batch",
                List.of(Workflow.step("step.a", Map.of())),
                Workflow.step("on.complete", Map.of()),
                Workflow.step("on.success", Map.of()),
                Workflow.step("on.failure", Map.of()));

        assertSame(expected, result);
    }

    @Test
    void getWorkflowDelegatesToClient() {
        var expected = createWorkflowStatus("wf-5", "chain");
        when(client.getWorkflow("wf-5")).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.getWorkflow("wf-5");

        assertSame(expected, result);
        verify(client).getWorkflow("wf-5");
    }

    @Test
    void cancelWorkflowDelegatesToClient() {
        var template = new OjsWorkflowTemplate(client);
        template.cancelWorkflow("wf-6");
        verify(client).cancelWorkflow("wf-6");
    }

    @Test
    void getClientReturnsUnderlying() {
        var template = new OjsWorkflowTemplate(client);
        assertSame(client, template.getClient());
    }

    @Test
    void constructorRejectsNullClient() {
        assertThrows(NullPointerException.class, () -> new OjsWorkflowTemplate(null));
    }

    @Test
    void fluentBuilderChainDispatches() {
        var expected = createWorkflowStatus("wf-7", "chain");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.workflow("my-chain")
                .chain()
                .step("step.first", Map.of("k", "v"))
                .step("step.second", Map.of())
                .dispatch();

        assertSame(expected, result);
        verify(client).createWorkflow(any(Workflow.Definition.class));
    }

    @Test
    void fluentBuilderGroupDispatches() {
        var expected = createWorkflowStatus("wf-8", "group");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.workflow("my-group")
                .group()
                .step("a", Map.of())
                .step("b", Map.of())
                .dispatch();

        assertSame(expected, result);
    }

    @Test
    void fluentBuilderBatchWithCallbacksDispatches() {
        var expected = createWorkflowStatus("wf-9", "batch");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.workflow("my-batch")
                .batch()
                .step("work.a", Map.of())
                .step("work.b", Map.of())
                .onComplete("done", Map.of())
                .onSuccess("success", Map.of())
                .onFailure("fail", Map.of())
                .dispatch();

        assertSame(expected, result);
    }

    @Test
    void fluentBuilderWithQueueStep() {
        var expected = createWorkflowStatus("wf-10", "chain");
        when(client.createWorkflow(any(Workflow.Definition.class))).thenReturn(expected);

        var template = new OjsWorkflowTemplate(client);
        var result = template.workflow("queued")
                .chain()
                .step("step.a", Map.of(), "high-priority")
                .dispatch();

        assertSame(expected, result);
    }

    @Test
    void fluentBuilderThrowsWithoutMode() {
        var template = new OjsWorkflowTemplate(client);
        var builder = template.workflow("no-mode")
                .step("step.a", Map.of());

        assertThrows(IllegalStateException.class, builder::dispatch);
    }

    private static Workflow.WorkflowStatus createWorkflowStatus(String id, String type) {
        return new Workflow.WorkflowStatus(id, "test-" + type, "active", null, null, List.of());
    }
}
