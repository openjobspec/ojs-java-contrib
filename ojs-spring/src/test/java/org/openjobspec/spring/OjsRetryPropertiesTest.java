package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.RetryPolicy;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OjsRetryPropertiesTest {

    @Test
    void defaultValues() {
        var props = new OjsRetryProperties();

        assertEquals(3, props.getMaxAttempts());
        assertEquals(Duration.ofSeconds(1), props.getInitialInterval());
        assertEquals(2.0, props.getBackoffCoefficient());
        assertEquals(Duration.ofMinutes(5), props.getMaxInterval());
        assertTrue(props.isJitter());
        assertTrue(props.getNonRetryableErrors().isEmpty());
        assertEquals("discard", props.getOnExhaustion());
    }

    @Test
    void settersWork() {
        var props = new OjsRetryProperties();
        props.setMaxAttempts(5);
        props.setInitialInterval(Duration.ofSeconds(2));
        props.setBackoffCoefficient(3.0);
        props.setMaxInterval(Duration.ofMinutes(10));
        props.setJitter(false);
        props.setNonRetryableErrors(List.of("ValidationError"));
        props.setOnExhaustion("dlq");

        assertEquals(5, props.getMaxAttempts());
        assertEquals(Duration.ofSeconds(2), props.getInitialInterval());
        assertEquals(3.0, props.getBackoffCoefficient());
        assertEquals(Duration.ofMinutes(10), props.getMaxInterval());
        assertFalse(props.isJitter());
        assertEquals(List.of("ValidationError"), props.getNonRetryableErrors());
        assertEquals("dlq", props.getOnExhaustion());
    }

    @Test
    void toRetryPolicyConvertsCorrectly() {
        var props = new OjsRetryProperties();
        props.setMaxAttempts(5);
        props.setInitialInterval(Duration.ofSeconds(2));
        props.setBackoffCoefficient(3.0);
        props.setMaxInterval(Duration.ofMinutes(10));
        props.setJitter(false);
        props.setNonRetryableErrors(List.of("ValidationError", "NotFoundError"));
        props.setOnExhaustion("dlq");

        RetryPolicy policy = props.toRetryPolicy();

        assertNotNull(policy);
        assertEquals(5, policy.maxAttempts());
        assertEquals(Duration.ofSeconds(2), policy.initialInterval());
        assertEquals(3.0, policy.backoffCoefficient());
        assertEquals(Duration.ofMinutes(10), policy.maxInterval());
        assertFalse(policy.jitter());
        assertEquals(List.of("ValidationError", "NotFoundError"), policy.nonRetryableErrors());
        assertEquals("dlq", policy.onExhaustion());
    }

    @Test
    void toRetryPolicyWithDefaults() {
        var props = new OjsRetryProperties();
        RetryPolicy policy = props.toRetryPolicy();

        assertNotNull(policy);
        assertEquals(3, policy.maxAttempts());
        assertEquals(Duration.ofSeconds(1), policy.initialInterval());
        assertEquals(2.0, policy.backoffCoefficient());
        assertEquals(Duration.ofMinutes(5), policy.maxInterval());
        assertTrue(policy.jitter());
        assertTrue(policy.nonRetryableErrors().isEmpty());
        assertEquals("discard", policy.onExhaustion());
    }
}
