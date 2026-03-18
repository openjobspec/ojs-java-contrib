package org.openjobspec.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.Workflow;
import org.openjobspec.ojs.Workflow.Step;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CDI service that exposes OJS workflow operations (chain, group, batch)
 * through a convenient injectable bean.
 *
 * <pre>{@code
 * @Inject OjsWorkflowService workflows;
 *
 * var status = workflows.chain("onboarding",
 *     workflows.step("user.create", Map.of("email", "a@b.com")),
 *     workflows.step("email.welcome", Map.of())
 * );
 * }</pre>
 */
@ApplicationScoped
public class OjsWorkflowService {

    @Inject
    OJSClient client;

    // ---- step builders ----

    /** Create a workflow step. */
    public Step step(String type, Map<String, Object> args) {
        return Workflow.step(type, args);
    }

    /** Create a workflow step targeted at a specific queue. */
    public Step step(String type, Map<String, Object> args, String queue) {
        return Workflow.step(type, args, queue);
    }

    // ---- synchronous workflow creation ----

    /**
     * Create and submit a sequential (chain) workflow.
     *
     * @param name  workflow name
     * @param steps ordered steps to execute sequentially
     * @return the workflow status including its server-assigned ID
     */
    public Workflow.WorkflowStatus chain(String name, Step... steps) {
        return client.createWorkflow(Workflow.chain(name, steps));
    }

    /**
     * Create and submit a parallel (group) workflow.
     *
     * @param name  workflow name
     * @param steps steps to execute in parallel (fan-out / fan-in)
     * @return the workflow status
     */
    public Workflow.WorkflowStatus group(String name, Step... steps) {
        return client.createWorkflow(Workflow.group(name, steps));
    }

    /**
     * Create and submit a batch workflow with lifecycle callbacks.
     *
     * @param name      workflow name
     * @param callbacks built callbacks (use {@link Workflow#callbacks()} builder)
     * @param steps     steps to execute in parallel
     * @return the workflow status
     */
    public Workflow.WorkflowStatus batch(String name, Workflow.CallbacksBuilder callbacks, Step... steps) {
        return client.createWorkflow(Workflow.batch(name, callbacks, steps));
    }

    /**
     * Submit an arbitrary workflow definition.
     */
    public Workflow.WorkflowStatus createWorkflow(Workflow.Definition definition) {
        return client.createWorkflow(definition);
    }

    // ---- queries ----

    /** Retrieve the current status of a workflow. */
    public Workflow.WorkflowStatus getWorkflow(String id) {
        return client.getWorkflow(id);
    }

    /** Cancel a running workflow. */
    public void cancelWorkflow(String id) {
        client.cancelWorkflow(id);
    }

    // ---- async variants ----

    /** Asynchronous version of {@link #chain(String, Step...)}. */
    public CompletableFuture<Workflow.WorkflowStatus> chainAsync(String name, Step... steps) {
        return client.createWorkflowAsync(Workflow.chain(name, steps));
    }

    /** Asynchronous version of {@link #group(String, Step...)}. */
    public CompletableFuture<Workflow.WorkflowStatus> groupAsync(String name, Step... steps) {
        return client.createWorkflowAsync(Workflow.group(name, steps));
    }

    /** Asynchronous version of {@link #createWorkflow(Workflow.Definition)}. */
    public CompletableFuture<Workflow.WorkflowStatus> createWorkflowAsync(Workflow.Definition definition) {
        return client.createWorkflowAsync(definition);
    }
}
