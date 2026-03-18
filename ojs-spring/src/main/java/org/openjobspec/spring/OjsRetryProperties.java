package org.openjobspec.spring;

import org.openjobspec.ojs.RetryPolicy;

import java.time.Duration;
import java.util.List;

/**
 * Maps Spring Boot properties to an OJS {@link RetryPolicy}.
 *
 * <p>Configured under {@code ojs.retry.*}:
 * <pre>{@code
 * ojs:
 *   retry:
 *     max-attempts: 5
 *     initial-interval: 2s
 *     backoff-coefficient: 2.0
 *     max-interval: 10m
 *     jitter: true
 *     on-exhaustion: discard
 * }</pre>
 */
public class OjsRetryProperties {

    /** Maximum number of retry attempts. */
    private int maxAttempts = 3;

    /** Initial interval before the first retry. */
    private Duration initialInterval = Duration.ofSeconds(1);

    /** Backoff multiplier applied between retries. */
    private double backoffCoefficient = 2.0;

    /** Maximum interval cap for backoff. */
    private Duration maxInterval = Duration.ofMinutes(5);

    /** Whether to add random jitter to retry delays. */
    private boolean jitter = true;

    /** Errors that should not be retried. */
    private List<String> nonRetryableErrors = List.of();

    /** Action when retries are exhausted: "discard" or "dlq". */
    private String onExhaustion = "discard";

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Duration getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(Duration initialInterval) {
        this.initialInterval = initialInterval;
    }

    public double getBackoffCoefficient() {
        return backoffCoefficient;
    }

    public void setBackoffCoefficient(double backoffCoefficient) {
        this.backoffCoefficient = backoffCoefficient;
    }

    public Duration getMaxInterval() {
        return maxInterval;
    }

    public void setMaxInterval(Duration maxInterval) {
        this.maxInterval = maxInterval;
    }

    public boolean isJitter() {
        return jitter;
    }

    public void setJitter(boolean jitter) {
        this.jitter = jitter;
    }

    public List<String> getNonRetryableErrors() {
        return nonRetryableErrors;
    }

    public void setNonRetryableErrors(List<String> nonRetryableErrors) {
        this.nonRetryableErrors = nonRetryableErrors;
    }

    public String getOnExhaustion() {
        return onExhaustion;
    }

    public void setOnExhaustion(String onExhaustion) {
        this.onExhaustion = onExhaustion;
    }

    /**
     * Convert these properties to an OJS SDK {@link RetryPolicy}.
     *
     * @return the retry policy
     */
    public RetryPolicy toRetryPolicy() {
        return RetryPolicy.builder()
                .maxAttempts(maxAttempts)
                .initialInterval(initialInterval)
                .backoffCoefficient(backoffCoefficient)
                .maxInterval(maxInterval)
                .jitter(jitter)
                .nonRetryableErrors(nonRetryableErrors)
                .onExhaustion(onExhaustion)
                .build();
    }
}
