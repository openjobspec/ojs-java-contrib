package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.Workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Spring-managed service for building and dispatching OJS workflows.
 *
 * <p>Provides direct methods for the three workflow primitives (chain, group, batch)
 * and a fluent {@link WorkflowBuilder} for complex compositions.
 *
 * <pre>{@code
 * @Autowired OjsWorkflowTemplate workflows;
 *
 * // Sequential pipeline
 * workflows.chain("order-pipeline",
 *     Workflow.step("order.validate", Map.of("orderId", 42)),
 *     Workflow.step("order.charge", Map.of("orderId", 42)),
 *     Workflow.step("order.ship", Map.of("orderId", 42)));
 *
 * // Parallel fan-out
 * workflows.group("notifications",
 *     Workflow.step("notify.email", Map.of("to", "a@b.com")),
 *     Workflow.step("notify.sms", Map.of("phone", "+1234")));
 *
 * // Batch with callbacks
 * workflows.batch("image-resize",
 *     List.of(
 *         Workflow.step("image.resize", Map.of("size", "sm")),
 *         Workflow.step("image.resize", Map.of("size", "md"))),
 *     Workflow.step("image.complete", Map.of()),
 *     null, null);
 *
 * // Fluent builder
 * workflows.workflow("complex-flow")
 *     .chain()
 *     .step("step.first", Map.of("key", "val"))
 *     .step("step.second", Map.of())
 *     .dispatch();
 * }</pre>
 */
public class OjsWorkflowTemplate {

    private final OJSClient client;

    public OjsWorkflowTemplate(OJSClient client) {
        this.client = Objects.requireNonNull(client, "client must not be null");
    }

    /** Get the underlying OJS client. */
    public OJSClient getClient() {
        return client;
    }

    /**
     * Execute a chain workflow (sequential execution).
     *
     * @param name  the workflow name
     * @param steps the steps to execute in order
     * @return the workflow status response
     */
    public Workflow.WorkflowStatus chain(String name, Workflow.Step... steps) {
        return client.createWorkflow(Workflow.chain(name, steps));
    }

    /**
     * Execute a group workflow (parallel fan-out).
     *
     * @param name  the workflow name
     * @param steps the steps to execute in parallel
     * @return the workflow status response
     */
    public Workflow.WorkflowStatus group(String name, Workflow.Step... steps) {
        return client.createWorkflow(Workflow.group(name, steps));
    }

    /**
     * Execute a batch workflow (parallel execution with callbacks).
     *
     * @param name       the workflow name
     * @param steps      the batch steps
     * @param onComplete callback step when all steps finish (nullable)
     * @param onSuccess  callback step when all steps succeed (nullable)
     * @param onFailure  callback step when any step fails (nullable)
     * @return the workflow status response
     */
    public Workflow.WorkflowStatus batch(String name,
                                         List<Workflow.Step> steps,
                                         Workflow.Step onComplete,
                                         Workflow.Step onSuccess,
                                         Workflow.Step onFailure) {
        var callbacksBuilder = Workflow.callbacks();
        if (onComplete != null) callbacksBuilder.onComplete(onComplete);
        if (onSuccess != null) callbacksBuilder.onSuccess(onSuccess);
        if (onFailure != null) callbacksBuilder.onFailure(onFailure);
        return client.createWorkflow(
                Workflow.batch(name, callbacksBuilder, steps.toArray(Workflow.Step[]::new)));
    }

    /**
     * Get the status of an existing workflow.
     *
     * @param workflowId the workflow ID
     * @return the workflow status
     */
    public Workflow.WorkflowStatus getWorkflow(String workflowId) {
        return client.getWorkflow(workflowId);
    }

    /**
     * Cancel a running workflow.
     *
     * @param workflowId the workflow ID
     */
    public void cancelWorkflow(String workflowId) {
        client.cancelWorkflow(workflowId);
    }

    /**
     * Start building a workflow with the fluent API.
     *
     * @param name the workflow name
     * @return a new workflow builder
     */
    public WorkflowBuilder workflow(String name) {
        return new WorkflowBuilder(client, name);
    }

    /**
     * Fluent builder for composing and dispatching OJS workflows.
     */
    public static class WorkflowBuilder {

        private final OJSClient client;
        private final String name;
        private final List<Workflow.Step> steps = new ArrayList<>();
        private Mode mode;
        private Workflow.Step onComplete;
        private Workflow.Step onSuccess;
        private Workflow.Step onFailure;

        private enum Mode { CHAIN, GROUP, BATCH }

        WorkflowBuilder(OJSClient client, String name) {
            this.client = client;
            this.name = name;
        }

        /** Configure as a chain (sequential) workflow. */
        public WorkflowBuilder chain() {
            this.mode = Mode.CHAIN;
            return this;
        }

        /** Configure as a group (parallel) workflow. */
        public WorkflowBuilder group() {
            this.mode = Mode.GROUP;
            return this;
        }

        /** Configure as a batch (parallel + callbacks) workflow. */
        public WorkflowBuilder batch() {
            this.mode = Mode.BATCH;
            return this;
        }

        /** Add a step with type and arguments. */
        public WorkflowBuilder step(String type, Map<String, Object> args) {
            this.steps.add(Workflow.step(type, args));
            return this;
        }

        /** Add a step with type, arguments, and target queue. */
        public WorkflowBuilder step(String type, Map<String, Object> args, String queue) {
            this.steps.add(Workflow.step(type, args, queue));
            return this;
        }

        /** Set the batch onComplete callback. */
        public WorkflowBuilder onComplete(String type, Map<String, Object> args) {
            this.onComplete = Workflow.step(type, args);
            return this;
        }

        /** Set the batch onSuccess callback. */
        public WorkflowBuilder onSuccess(String type, Map<String, Object> args) {
            this.onSuccess = Workflow.step(type, args);
            return this;
        }

        /** Set the batch onFailure callback. */
        public WorkflowBuilder onFailure(String type, Map<String, Object> args) {
            this.onFailure = Workflow.step(type, args);
            return this;
        }

        /**
         * Build the workflow definition and dispatch it.
         *
         * @return the workflow status
         * @throws IllegalStateException if mode is not set
         */
        public Workflow.WorkflowStatus dispatch() {
            if (mode == null) {
                throw new IllegalStateException("Workflow mode not set. Call chain(), group(), or batch() first.");
            }
            var stepsArray = steps.toArray(Workflow.Step[]::new);
            var definition = switch (mode) {
                case CHAIN -> Workflow.chain(name, stepsArray);
                case GROUP -> Workflow.group(name, stepsArray);
                case BATCH -> {
                    var cb = Workflow.callbacks();
                    if (onComplete != null) cb.onComplete(onComplete);
                    if (onSuccess != null) cb.onSuccess(onSuccess);
                    if (onFailure != null) cb.onFailure(onFailure);
                    yield Workflow.batch(name, cb, stepsArray);
                }
            };
            return client.createWorkflow(definition);
        }
    }
}
