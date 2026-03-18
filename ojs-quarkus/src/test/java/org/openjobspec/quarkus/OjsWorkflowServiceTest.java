package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.Workflow;
import org.openjobspec.ojs.Workflow.Step;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class OjsWorkflowServiceTest {

    @Test
    void isApplicationScoped() {
        assertTrue(OjsWorkflowService.class.isAnnotationPresent(
                jakarta.enterprise.context.ApplicationScoped.class));
    }

    @Test
    void hasClientField() throws NoSuchFieldException {
        var field = OjsWorkflowService.class.getDeclaredField("client");
        assertTrue(field.isAnnotationPresent(jakarta.inject.Inject.class));
        assertEquals(OJSClient.class, field.getType());
    }

    @Test
    void hasChainMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "chain", String.class, Step[].class);
        assertEquals(Workflow.WorkflowStatus.class, method.getReturnType());
    }

    @Test
    void hasGroupMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "group", String.class, Step[].class);
        assertEquals(Workflow.WorkflowStatus.class, method.getReturnType());
    }

    @Test
    void hasBatchMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "batch", String.class, Workflow.CallbacksBuilder.class, Step[].class);
        assertEquals(Workflow.WorkflowStatus.class, method.getReturnType());
    }

    @Test
    void hasCreateWorkflowMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "createWorkflow", Workflow.Definition.class);
        assertEquals(Workflow.WorkflowStatus.class, method.getReturnType());
    }

    @Test
    void hasGetWorkflowMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "getWorkflow", String.class);
        assertEquals(Workflow.WorkflowStatus.class, method.getReturnType());
    }

    @Test
    void hasCancelWorkflowMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "cancelWorkflow", String.class);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void hasChainAsyncMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "chainAsync", String.class, Step[].class);
        assertEquals(CompletableFuture.class, method.getReturnType());
    }

    @Test
    void hasGroupAsyncMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "groupAsync", String.class, Step[].class);
        assertEquals(CompletableFuture.class, method.getReturnType());
    }

    @Test
    void hasStepBuilderMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "step", String.class, Map.class);
        assertEquals(Step.class, method.getReturnType());
    }

    @Test
    void hasStepBuilderWithQueueMethod() throws NoSuchMethodException {
        var method = OjsWorkflowService.class.getDeclaredMethod(
                "step", String.class, Map.class, String.class);
        assertEquals(Step.class, method.getReturnType());
    }

    @Test
    void canBeInstantiated() {
        var service = new OjsWorkflowService();
        assertNotNull(service);
    }
}
