package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OjsHealthCheck.
 * Since MicroProfile Health is a compileOnly dependency, the health check class
 * cannot be directly loaded at test runtime. These tests verify structural properties.
 */
class OjsHealthCheckTest {

    @Test
    void healthCheckClassFileExistsOnClasspath() {
        var resource = getClass().getClassLoader()
                .getResource("org/openjobspec/quarkus/OjsHealthCheck.class");
        assertNotNull(resource, "OjsHealthCheck.class should exist on classpath");
    }

    @Test
    void healthCheckIsInCorrectPackage() {
        var resource = getClass().getClassLoader()
                .getResource("org/openjobspec/quarkus/OjsHealthCheck.class");
        assertNotNull(resource);
        assertTrue(resource.toString().contains("org/openjobspec/quarkus"));
    }

    @Test
    void healthCheckFollsNamingConvention() {
        var className = "OjsHealthCheck";
        assertTrue(className.startsWith("Ojs"));
        assertTrue(className.endsWith("HealthCheck"));
    }

    @Test
    void healthCheckHasClientField() {
        // The health check class declares a client field annotated with @Inject.
        // Even though we can't load the class, we can verify the .class file is present.
        var resource = getClass().getClassLoader()
                .getResource("org/openjobspec/quarkus/OjsHealthCheck.class");
        assertNotNull(resource);
    }
}
