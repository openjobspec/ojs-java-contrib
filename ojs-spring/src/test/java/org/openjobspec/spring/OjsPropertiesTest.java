package org.openjobspec.spring;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OjsPropertiesTest {

    @Test
    void defaultValues() {
        var props = new OjsProperties();
        assertEquals("http://localhost:8080", props.getUrl());
        assertEquals("default", props.getDefaultQueue());
        assertEquals(List.of("default"), props.getQueues());
        assertEquals(10, props.getConcurrency());
        assertTrue(props.isEnabled());
        assertNotNull(props.getWorker());
        assertNotNull(props.getRetry());
    }

    @Test
    void resolvedConcurrencyPrefersNestedWorker() {
        var props = new OjsProperties();
        props.setConcurrency(10);
        props.getWorker().setConcurrency(32);

        assertEquals(32, props.resolvedConcurrency());
    }

    @Test
    void resolvedConcurrencyFallsBackToTopLevel() {
        var props = new OjsProperties();
        props.setConcurrency(20);

        assertEquals(20, props.resolvedConcurrency());
    }

    @Test
    void resolvedQueuesPrefersNestedWorker() {
        var props = new OjsProperties();
        props.setQueues(List.of("default"));
        props.getWorker().setQueues(List.of("critical", "background"));

        assertEquals(List.of("critical", "background"), props.resolvedQueues());
    }

    @Test
    void resolvedQueuesFallsBackToTopLevel() {
        var props = new OjsProperties();
        props.setQueues(List.of("alpha", "beta"));

        assertEquals(List.of("alpha", "beta"), props.resolvedQueues());
    }

    @Test
    void retryDefaults() {
        var props = new OjsProperties();
        assertEquals(3, props.getRetry().getMaxAttempts());
        assertEquals("exponential", props.getRetry().getBackoff());
    }

    @Test
    void retryCustomValues() {
        var props = new OjsProperties();
        props.getRetry().setMaxAttempts(5);
        props.getRetry().setBackoff("fixed");

        assertEquals(5, props.getRetry().getMaxAttempts());
        assertEquals("fixed", props.getRetry().getBackoff());
    }

    @Test
    void settersWork() {
        var props = new OjsProperties();
        props.setUrl("http://custom:9090");
        props.setDefaultQueue("emails");
        props.setEnabled(false);

        assertEquals("http://custom:9090", props.getUrl());
        assertEquals("emails", props.getDefaultQueue());
        assertFalse(props.isEnabled());
    }
}
