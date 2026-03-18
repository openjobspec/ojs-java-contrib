package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class OjsEventBridgeTest {

    @Test
    void isApplicationScoped() {
        assertTrue(OjsEventBridge.class.isAnnotationPresent(
                jakarta.enterprise.context.ApplicationScoped.class));
    }

    @Test
    void hasConfigField() throws NoSuchFieldException {
        var field = OjsEventBridge.class.getDeclaredField("config");
        assertTrue(field.isAnnotationPresent(jakarta.inject.Inject.class));
    }

    @Test
    void hasCdiEventField() throws NoSuchFieldException {
        var field = OjsEventBridge.class.getDeclaredField("cdiEvent");
        assertTrue(field.isAnnotationPresent(jakarta.inject.Inject.class));
        assertEquals(jakarta.enterprise.event.Event.class, field.getType());
    }

    @Test
    void hasOnStartMethod() {
        boolean found = false;
        for (Method m : OjsEventBridge.class.getDeclaredMethods()) {
            if (m.getName().equals("onStart")) {
                found = true;
                var params = m.getParameters();
                assertEquals(1, params.length);
                assertTrue(params[0].isAnnotationPresent(
                        jakarta.enterprise.event.Observes.class));
                assertEquals(io.quarkus.runtime.StartupEvent.class,
                        params[0].getType());
                break;
            }
        }
        assertTrue(found, "Should have onStart method");
    }

    @Test
    void hasOnStopMethod() {
        boolean found = false;
        for (Method m : OjsEventBridge.class.getDeclaredMethods()) {
            if (m.getName().equals("onStop")) {
                found = true;
                var params = m.getParameters();
                assertEquals(1, params.length);
                assertTrue(params[0].isAnnotationPresent(
                        jakarta.enterprise.event.Observes.class));
                assertEquals(io.quarkus.runtime.ShutdownEvent.class,
                        params[0].getType());
                break;
            }
        }
        assertTrue(found, "Should have onStop method");
    }

    @Test
    void hasIsSubscribedMethod() throws NoSuchMethodException {
        var method = OjsEventBridge.class.getDeclaredMethod("isSubscribed");
        assertEquals(boolean.class, method.getReturnType());
    }

    @Test
    void canBeInstantiated() {
        var bridge = new OjsEventBridge();
        assertNotNull(bridge);
        assertFalse(bridge.isSubscribed());
    }
}
