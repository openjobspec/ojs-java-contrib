package org.openjobspec.quarkus;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OjsConfigTest {

    @Test
    void hasConfigMappingAnnotation() {
        assertTrue(OjsConfig.class.isAnnotationPresent(ConfigMapping.class));
    }

    @Test
    void configMappingPrefixIsOjs() {
        var mapping = OjsConfig.class.getAnnotation(ConfigMapping.class);
        assertEquals("ojs", mapping.prefix());
    }

    @Test
    void urlHasDefaultValue() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("url");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("http://localhost:8080", annotation.value());
    }

    @Test
    void queuesHasDefaultValue() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("queues");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("default", annotation.value());
    }

    @Test
    void concurrencyHasDefaultValue() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("concurrency");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("10", annotation.value());
    }

    @Test
    void urlReturnsString() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("url");
        assertEquals(String.class, method.getReturnType());
    }

    @Test
    void queuesReturnsList() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("queues");
        assertEquals(List.class, method.getReturnType());
    }

    @Test
    void concurrencyReturnsInt() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("concurrency");
        assertEquals(int.class, method.getReturnType());
    }

    @Test
    void configIsInterface() {
        assertTrue(OjsConfig.class.isInterface());
    }

    // ---- Worker nested config ----

    @Test
    void workerMethodReturnsNestedInterface() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("worker");
        assertEquals(OjsConfig.Worker.class, method.getReturnType());
    }

    @Test
    void workerIsInterface() {
        assertTrue(OjsConfig.Worker.class.isInterface());
    }

    @Test
    void workerGracePeriodSecondsDefault() throws NoSuchMethodException {
        var method = OjsConfig.Worker.class.getDeclaredMethod("gracePeriodSeconds");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("25", annotation.value());
        assertEquals(int.class, method.getReturnType());
    }

    @Test
    void workerPollIntervalSecondsDefault() throws NoSuchMethodException {
        var method = OjsConfig.Worker.class.getDeclaredMethod("pollIntervalSeconds");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("1", annotation.value());
        assertEquals(int.class, method.getReturnType());
    }

    @Test
    void workerAutoStartDefault() throws NoSuchMethodException {
        var method = OjsConfig.Worker.class.getDeclaredMethod("autoStart");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("true", annotation.value());
        assertEquals(boolean.class, method.getReturnType());
    }

    // ---- Encryption nested config ----

    @Test
    void encryptionMethodReturnsNestedInterface() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("encryption");
        assertEquals(OjsConfig.Encryption.class, method.getReturnType());
    }

    @Test
    void encryptionIsInterface() {
        assertTrue(OjsConfig.Encryption.class.isInterface());
    }

    @Test
    void encryptionEnabledDefault() throws NoSuchMethodException {
        var method = OjsConfig.Encryption.class.getDeclaredMethod("enabled");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("false", annotation.value());
        assertEquals(boolean.class, method.getReturnType());
    }

    @Test
    void encryptionKeyReturnsOptional() throws NoSuchMethodException {
        var method = OjsConfig.Encryption.class.getDeclaredMethod("key");
        assertEquals(Optional.class, method.getReturnType());
    }

    @Test
    void encryptionKeyIdDefault() throws NoSuchMethodException {
        var method = OjsConfig.Encryption.class.getDeclaredMethod("keyId");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("default", annotation.value());
        assertEquals(String.class, method.getReturnType());
    }

    // ---- Events nested config ----

    @Test
    void eventsMethodReturnsNestedInterface() throws NoSuchMethodException {
        var method = OjsConfig.class.getDeclaredMethod("events");
        assertEquals(OjsConfig.Events.class, method.getReturnType());
    }

    @Test
    void eventsIsInterface() {
        assertTrue(OjsConfig.Events.class.isInterface());
    }

    @Test
    void eventsEnabledDefault() throws NoSuchMethodException {
        var method = OjsConfig.Events.class.getDeclaredMethod("enabled");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("false", annotation.value());
        assertEquals(boolean.class, method.getReturnType());
    }

    @Test
    void eventsChannelDefault() throws NoSuchMethodException {
        var method = OjsConfig.Events.class.getDeclaredMethod("channel");
        var annotation = method.getAnnotation(WithDefault.class);
        assertNotNull(annotation);
        assertEquals("events", annotation.value());
        assertEquals(String.class, method.getReturnType());
    }
}
