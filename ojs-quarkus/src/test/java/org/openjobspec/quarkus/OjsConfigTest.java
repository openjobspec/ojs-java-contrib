package org.openjobspec.quarkus;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

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
}
