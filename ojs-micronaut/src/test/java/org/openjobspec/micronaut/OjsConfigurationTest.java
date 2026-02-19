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
}
