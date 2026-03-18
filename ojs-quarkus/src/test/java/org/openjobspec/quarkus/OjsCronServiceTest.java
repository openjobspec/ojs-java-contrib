package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.OJSClient;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OjsCronServiceTest {

    @Test
    void isApplicationScoped() {
        assertTrue(OjsCronService.class.isAnnotationPresent(
                jakarta.enterprise.context.ApplicationScoped.class));
    }

    @Test
    void hasClientField() throws NoSuchFieldException {
        var field = OjsCronService.class.getDeclaredField("client");
        assertTrue(field.isAnnotationPresent(jakarta.inject.Inject.class));
        assertEquals(OJSClient.class, field.getType());
    }

    @Test
    void hasListMethod() throws NoSuchMethodException {
        var method = OjsCronService.class.getDeclaredMethod("list");
        assertEquals(List.class, method.getReturnType());
    }

    @Test
    void hasRegisterMethodWithMap() throws NoSuchMethodException {
        var method = OjsCronService.class.getDeclaredMethod("register", Map.class);
        assertEquals(Map.class, method.getReturnType());
    }

    @Test
    void hasConvenienceRegisterMethod() throws NoSuchMethodException {
        var method = OjsCronService.class.getDeclaredMethod(
                "register", String.class, String.class, String.class, List.class);
        assertEquals(Map.class, method.getReturnType());
    }

    @Test
    void hasUnregisterMethod() throws NoSuchMethodException {
        var method = OjsCronService.class.getDeclaredMethod("unregister", String.class);
        assertEquals(void.class, method.getReturnType());
    }

    @Test
    void canBeInstantiated() {
        var service = new OjsCronService();
        assertNotNull(service);
    }

    @Test
    void allPublicMethodsArePresent() {
        var publicMethods = java.util.Arrays.stream(OjsCronService.class.getDeclaredMethods())
                .filter(m -> java.lang.reflect.Modifier.isPublic(m.getModifiers()))
                .map(Method::getName)
                .toList();

        assertTrue(publicMethods.contains("list"));
        assertTrue(publicMethods.contains("register"));
        assertTrue(publicMethods.contains("unregister"));
    }
}
