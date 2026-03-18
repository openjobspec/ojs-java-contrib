package org.openjobspec.quarkus;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.List;
import java.util.Optional;

/**
 * Quarkus configuration mapping for OJS properties.
 *
 * <p>Core properties ({@code ojs.url}, {@code ojs.queues}, {@code ojs.concurrency})
 * configure the client and worker. Nested groups add encryption, event streaming,
 * and advanced worker tuning.</p>
 */
@ConfigMapping(prefix = "ojs")
public interface OjsConfig {

    /** OJS backend URL. */
    @WithDefault("http://localhost:8080")
    String url();

    /** Queues the worker should poll. */
    @WithDefault("default")
    List<String> queues();

    /** Worker concurrency. */
    @WithDefault("10")
    int concurrency();

    /** Advanced worker configuration. */
    Worker worker();

    /** Encryption configuration for job payloads. */
    Encryption encryption();

    /** Server-Sent Events streaming configuration. */
    Events events();

    /**
     * Advanced worker tuning beyond the top-level {@code ojs.queues}
     * and {@code ojs.concurrency} properties.
     */
    interface Worker {

        /** Grace period in seconds for in-flight jobs during shutdown. */
        @WithDefault("25")
        int gracePeriodSeconds();

        /** Poll interval in seconds between fetch cycles. */
        @WithDefault("1")
        int pollIntervalSeconds();

        /** Whether the worker starts automatically with the application. */
        @WithDefault("true")
        boolean autoStart();
    }

    /**
     * AES-256-GCM encryption for job arguments.
     *
     * <p>Set {@code ojs.encryption.enabled=true} and provide a Base64-encoded
     * 256-bit key via {@code ojs.encryption.key}.</p>
     */
    interface Encryption {

        /** Enable payload encryption. */
        @WithDefault("false")
        boolean enabled();

        /** Base64-encoded AES-256 key. */
        Optional<String> key();

        /** Logical key identifier for key rotation. */
        @WithDefault("default")
        String keyId();
    }

    /**
     * Server-Sent Events streaming configuration.
     *
     * <p>When enabled, the {@link OjsEventBridge} subscribes to the configured
     * channel and fires CDI {@link OjsJobEvent} instances.</p>
     */
    interface Events {

        /** Enable SSE event streaming. */
        @WithDefault("false")
        boolean enabled();

        /** SSE channel to subscribe to. */
        @WithDefault("events")
        String channel();
    }
}
