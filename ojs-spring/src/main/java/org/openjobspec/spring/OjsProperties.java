package org.openjobspec.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration properties for OJS, bound from {@code ojs.*} in application.yml/properties.
 *
 * <pre>{@code
 * ojs:
 *   url: http://localhost:8080
 *   default-queue: default
 *   worker:
 *     concurrency: 10
 *     queues:
 *       - default
 *       - email
 *   retry:
 *     max-attempts: 3
 *     backoff: exponential
 * }</pre>
 */
@ConfigurationProperties(prefix = "ojs")
public class OjsProperties {

    /** OJS backend URL. */
    private String url = "http://localhost:8080";

    /** Default queue for enqueued jobs. */
    private String defaultQueue = "default";

    /** Queues the worker should poll (top-level shorthand for worker.queues). */
    private List<String> queues = List.of("default");

    /** Worker concurrency (top-level shorthand for worker.concurrency). */
    private int concurrency = 10;

    /** Enable or disable OJS auto-configuration. */
    private boolean enabled = true;

    /** Worker configuration. */
    private Worker worker = new Worker();

    /** Retry configuration. */
    private Retry retry = new Retry();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDefaultQueue() {
        return defaultQueue;
    }

    public void setDefaultQueue(String defaultQueue) {
        this.defaultQueue = defaultQueue;
    }

    public List<String> getQueues() {
        return queues;
    }

    public void setQueues(List<String> queues) {
        this.queues = queues;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    /**
     * Resolve effective worker concurrency: prefer nested {@code worker.concurrency}
     * if explicitly set, otherwise fall back to top-level {@code concurrency}.
     */
    public int resolvedConcurrency() {
        return worker.getConcurrency() > 0 ? worker.getConcurrency() : concurrency;
    }

    /**
     * Resolve effective worker queues: prefer nested {@code worker.queues}
     * if explicitly set, otherwise fall back to top-level {@code queues}.
     */
    public List<String> resolvedQueues() {
        return worker.getQueues() != null && !worker.getQueues().isEmpty()
                ? worker.getQueues() : queues;
    }

    /** Worker-specific configuration properties. */
    public static class Worker {

        /** Number of virtual threads processing jobs concurrently. */
        private int concurrency = 0;

        /** Queues the worker should poll. */
        private List<String> queues = List.of();

        public int getConcurrency() {
            return concurrency;
        }

        public void setConcurrency(int concurrency) {
            this.concurrency = concurrency;
        }

        public List<String> getQueues() {
            return queues;
        }

        public void setQueues(List<String> queues) {
            this.queues = queues;
        }
    }

    /** Retry policy configuration properties. */
    public static class Retry {

        /** Maximum number of retry attempts. */
        private int maxAttempts = 3;

        /** Backoff strategy: "exponential" or "fixed". */
        private String backoff = "exponential";

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public String getBackoff() {
            return backoff;
        }

        public void setBackoff(String backoff) {
            this.backoff = backoff;
        }
    }
}
