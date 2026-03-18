package org.openjobspec.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OjsConfigurationTest {

    @Test
    void hasConfigurationPropertiesAnnotation() {
        assertTrue(OjsConfiguration.class.isAnnotationPresent(ConfigurationProperties.class));
    }

    @Test
    void configurationPropertiesPrefixIsOjs() {
        var annotation = OjsConfiguration.class.getAnnotation(ConfigurationProperties.class);
        assertEquals("ojs", annotation.value());
    }

    @Test
    void defaultUrlIsLocalhost() {
        var config = new OjsConfiguration();
        assertEquals("http://localhost:8080", config.getUrl());
    }

    @Test
    void defaultQueuesContainsDefault() {
        var config = new OjsConfiguration();
        assertEquals(List.of("default"), config.getQueues());
    }

    @Test
    void defaultConcurrencyIsTen() {
        var config = new OjsConfiguration();
        assertEquals(10, config.getConcurrency());
    }

    @Test
    void urlIsSettable() {
        var config = new OjsConfiguration();
        config.setUrl("http://remote:9090");
        assertEquals("http://remote:9090", config.getUrl());
    }

    @Test
    void queuesAreSettable() {
        var config = new OjsConfiguration();
        config.setQueues(List.of("high", "medium", "low"));
        assertEquals(3, config.getQueues().size());
        assertEquals("high", config.getQueues().get(0));
    }

    @Test
    void concurrencyIsSettable() {
        var config = new OjsConfiguration();
        config.setConcurrency(50);
        assertEquals(50, config.getConcurrency());
    }

    @Test
    void multiplePropertiesCanBeSetTogether() {
        var config = new OjsConfiguration();
        config.setUrl("http://prod:8080");
        config.setQueues(List.of("critical"));
        config.setConcurrency(100);

        assertEquals("http://prod:8080", config.getUrl());
        assertEquals(List.of("critical"), config.getQueues());
        assertEquals(100, config.getConcurrency());
    }

    // --- Worker enabled ---

    @Test
    void defaultWorkerEnabledIsTrue() {
        var config = new OjsConfiguration();
        assertTrue(config.isWorkerEnabled());
    }

    @Test
    void workerEnabledIsSettable() {
        var config = new OjsConfiguration();
        config.setWorkerEnabled(false);
        assertFalse(config.isWorkerEnabled());
    }

    // --- Events ---

    @Test
    void defaultEventsEnabledIsFalse() {
        var config = new OjsConfiguration();
        assertFalse(config.isEventsEnabled());
    }

    @Test
    void eventsEnabledIsSettable() {
        var config = new OjsConfiguration();
        config.setEventsEnabled(true);
        assertTrue(config.isEventsEnabled());
    }

    @Test
    void defaultEventsChannelIsWildcard() {
        var config = new OjsConfiguration();
        assertEquals("*", config.getEventsChannel());
    }

    @Test
    void eventsChannelIsSettable() {
        var config = new OjsConfiguration();
        config.setEventsChannel("jobs");
        assertEquals("jobs", config.getEventsChannel());
    }

    // --- Encryption ---

    @Test
    void defaultEncryptionEnabledIsFalse() {
        var config = new OjsConfiguration();
        assertFalse(config.isEncryptionEnabled());
    }

    @Test
    void encryptionEnabledIsSettable() {
        var config = new OjsConfiguration();
        config.setEncryptionEnabled(true);
        assertTrue(config.isEncryptionEnabled());
    }

    @Test
    void defaultEncryptionKeyIsNull() {
        var config = new OjsConfiguration();
        assertNull(config.getEncryptionKey());
    }

    @Test
    void encryptionKeyIsSettable() {
        var config = new OjsConfiguration();
        config.setEncryptionKey("dGVzdC1rZXk=");
        assertEquals("dGVzdC1rZXk=", config.getEncryptionKey());
    }

    @Test
    void defaultEncryptionKeyIdIsDefault() {
        var config = new OjsConfiguration();
        assertEquals("default", config.getEncryptionKeyId());
    }

    @Test
    void encryptionKeyIdIsSettable() {
        var config = new OjsConfiguration();
        config.setEncryptionKeyId("key-v2");
        assertEquals("key-v2", config.getEncryptionKeyId());
    }

    // --- All properties together ---

    @Test
    void allNewPropertiesCanBeSetTogether() {
        var config = new OjsConfiguration();
        config.setWorkerEnabled(false);
        config.setEventsEnabled(true);
        config.setEventsChannel("critical");
        config.setEncryptionEnabled(true);
        config.setEncryptionKey("c2VjcmV0");
        config.setEncryptionKeyId("prod-key");

        assertFalse(config.isWorkerEnabled());
        assertTrue(config.isEventsEnabled());
        assertEquals("critical", config.getEventsChannel());
        assertTrue(config.isEncryptionEnabled());
        assertEquals("c2VjcmV0", config.getEncryptionKey());
        assertEquals("prod-key", config.getEncryptionKeyId());
    }
}
