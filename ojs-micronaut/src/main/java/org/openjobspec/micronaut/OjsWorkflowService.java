package org.openjobspec.micronaut;

import jakarta.inject.Singleton;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.Workflow;
import org.openjobspec.ojs.Workflow.BatchCallbacks;
import org.openjobspec.ojs.Workflow.CallbacksBuilder;
import org.openjobspec.ojs.Workflow.Definition;
import org.openjobspec.ojs.Workflow.Step;
import org.openjobspec.ojs.Workflow.WorkflowStatus;

import java.util.Map;

/**
 * Micronaut service exposing OJS workflow operations.
 *
 * <p>Provides convenience methods for building and submitting workflows
 * (chains, groups, batches) using the OJS Java SDK's {@link Workflow} API.</p>
 *
 * <pre>{@code
 * @Inject OjsWorkflowService workflows;
 *
 * var status = workflows.submitChain("order-pipeline",
 *     workflows.step("order.validate", Map.of("orderId", 42)),
 *     workflows.step("order.charge", Map.of("orderId", 42)),
 *     workflows.step("order.ship", Map.of("orderId", 42))
 * );
 * }</pre>
 */
@Singleton
public class OjsWorkflowService {

    private final OJSClient client;

    public OjsWorkflowService(OJSClient client) {
        this.client = client;
    }

    /** Creates a workflow step with the given type and arguments. */
    public Step step(String type, Map<String, Object> args) {
        return Workflow.step(type, args);
    }

    /** Creates a workflow step with the given type, arguments, and target queue. */
    public Step step(String type, Map<String, Object> args, String queue) {
        return Workflow.step(type, args, queue);
    }

    /** Builds a sequential chain definition and submits it. */
    public WorkflowStatus submitChain(String name, Step... steps) {
        Definition def = Workflow.chain(name, steps);
        return client.createWorkflow(def);
    }

    /** Builds a parallel group definition and submits it. */
    public WorkflowStatus submitGroup(String name, Step... steps) {
        Definition def = Workflow.group(name, steps);
        return client.createWorkflow(def);
    }

    /** Builds a batch definition with callbacks and submits it. */
    public WorkflowStatus submitBatch(String name, CallbacksBuilder callbacks, Step... steps) {
        Definition def = Workflow.batch(name, callbacks, steps);
        return client.createWorkflow(def);
    }

    /** Returns a {@link CallbacksBuilder} for batch workflow callbacks. */
    public CallbacksBuilder callbacks() {
        return Workflow.callbacks();
    }

    /** Retrieves the status of an existing workflow by ID. */
    public WorkflowStatus getWorkflow(String workflowId) {
        return client.getWorkflow(workflowId);
    }

    /** Cancels a running workflow by ID. */
    public void cancelWorkflow(String workflowId) {
        client.cancelWorkflow(workflowId);
    }
}
