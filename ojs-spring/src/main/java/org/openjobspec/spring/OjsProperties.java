package org.openjobspec.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 *     max-attempts: 5
 *     initial-interval: 2s
 *     backoff-coefficient: 2.0
 *     max-interval: 10m
 *     jitter: true
 *     on-exhaustion: discard
 *   events:
 *     enabled: true
 *   cron:
 *     sync-on-startup: false
 *     definitions:
 *       - name: daily-report
 *         cron: "0 8 * * *"
 *         type: report.generate
 *         queue: reports
 *   encryption:
 *     enabled: true
 *     key: "base64-encoded-32-byte-AES-key"
 *     key-id: "key-2024"
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

    /** Events configuration. */
    private Events events = new Events();

    /** Cron configuration. */
    private Cron cron = new Cron();

    /** Encryption configuration. */
    private Encryption encryption = new Encryption();

    /** Retry policy configuration (maps to SDK RetryPolicy). */
    private OjsRetryProperties retryPolicy = new OjsRetryProperties();

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

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public Cron getCron() {
        return cron;
    }

    public void setCron(Cron cron) {
        this.cron = cron;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public OjsRetryProperties getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(OjsRetryProperties retryPolicy) {
        this.retryPolicy = retryPolicy;
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

    /** Events configuration properties. */
    public static class Events {

        /** Whether to enable the OJS event bridge. */
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /** Cron configuration properties. */
    public static class Cron {

        /** Whether to synchronize cron definitions on application startup. */
        private boolean syncOnStartup = false;

        /** Declarative cron job definitions. */
        private List<CronDefinition> definitions = new ArrayList<>();

        public boolean isSyncOnStartup() {
            return syncOnStartup;
        }

        public void setSyncOnStartup(boolean syncOnStartup) {
            this.syncOnStartup = syncOnStartup;
        }

        public List<CronDefinition> getDefinitions() {
            return definitions;
        }

        public void setDefinitions(List<CronDefinition> definitions) {
            this.definitions = definitions;
        }
    }

    /** A single cron job definition from properties. */
    public static class CronDefinition {

        /** Unique name for this cron job. */
        private String name;

        /** Cron expression (standard 5-field cron). */
        private String cron;

        /** The OJS job type to enqueue. */
        private String type;

        /** Target queue (optional). */
        private String queue;

        /** Job arguments (optional). */
        private Map<String, Object> args = Map.of();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public Map<String, Object> getArgs() {
            return args;
        }

        public void setArgs(Map<String, Object> args) {
            this.args = args;
        }
    }

    /** Encryption configuration properties. */
    public static class Encryption {

        /** Whether to enable AES-256-GCM encryption. */
        private boolean enabled = false;

        /** Base64-encoded 32-byte AES key. */
        private String key;

        /** Key identifier for key rotation. */
        private String keyId;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }
    }
}
