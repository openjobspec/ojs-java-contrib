package org.openjobspec.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.List;

/**
 * Micronaut configuration properties for OJS, bound from {@code ojs.*}.
 *
 * <p>Supports the following configuration keys:</p>
 * <ul>
 *   <li>{@code ojs.url} — Backend URL (default: {@code http://localhost:8080})</li>
 *   <li>{@code ojs.queues} — Worker poll queues (default: {@code [default]})</li>
 *   <li>{@code ojs.concurrency} — Worker concurrency (default: {@code 10})</li>
 *   <li>{@code ojs.worker.enabled} — Auto-start worker on server startup (default: {@code true})</li>
 *   <li>{@code ojs.events.enabled} — Enable SSE event bridge (default: {@code false})</li>
 *   <li>{@code ojs.events.channel} — SSE subscription channel (default: {@code *})</li>
 *   <li>{@code ojs.encryption.enabled} — Enable payload encryption (default: {@code false})</li>
 *   <li>{@code ojs.encryption.key} — Base64-encoded AES-256 key</li>
 *   <li>{@code ojs.encryption.key-id} — Key identifier for rotation</li>
 * </ul>
 */
@ConfigurationProperties("ojs")
public class OjsConfiguration {

    /** OJS backend URL. */
    private String url = "http://localhost:8080";

    /** Queues the worker should poll. */
    private List<String> queues = List.of("default");

    /** Worker concurrency. */
    private int concurrency = 10;

    /** Whether the worker should auto-start on server startup. */
    private boolean workerEnabled = true;

    /** Whether the SSE event bridge is enabled. */
    private boolean eventsEnabled = false;

    /** SSE channel to subscribe to ({@code *} for all events). */
    private String eventsChannel = "*";

    /** Whether encryption middleware is enabled. */
    private boolean encryptionEnabled = false;

    /** Base64-encoded AES-256 encryption key. */
    private String encryptionKey;

    /** Key identifier used for key rotation. */
    private String encryptionKeyId = "default";

    // --- url ---

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // --- queues ---

    public List<String> getQueues() {
        return queues;
    }

    public void setQueues(List<String> queues) {
        this.queues = queues;
    }

    // --- concurrency ---

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    // --- worker.enabled ---

    public boolean isWorkerEnabled() {
        return workerEnabled;
    }

    public void setWorkerEnabled(boolean workerEnabled) {
        this.workerEnabled = workerEnabled;
    }

    // --- events.enabled ---

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        this.eventsEnabled = eventsEnabled;
    }

    // --- events.channel ---

    public String getEventsChannel() {
        return eventsChannel;
    }

    public void setEventsChannel(String eventsChannel) {
        this.eventsChannel = eventsChannel;
    }

    // --- encryption.enabled ---

    public boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public void setEncryptionEnabled(boolean encryptionEnabled) {
        this.encryptionEnabled = encryptionEnabled;
    }

    // --- encryption.key ---

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    // --- encryption.key-id ---

    public String getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(String encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }
}
