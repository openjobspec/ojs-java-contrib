package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class OjsWorkerLifecycleTest {

    @Test
    void isApplicationScoped() {
        assertTrue(OjsWorkerLifecycle.class.isAnnotationPresent(
                jakarta.enterprise.context.ApplicationScoped.class));
    }

    @Test
    void hasOnStartMethod() {
        boolean found = false;
        for (Method m : OjsWorkerLifecycle.class.getDeclaredMethods()) {
            if (m.getName().equals("onStart")) {
                found = true;
                assertEquals(1, m.getParameterCount());
                break;
            }
        }
        assertTrue(found, "Should have onStart method");
    }

    @Test
    void hasOnStopMethod() {
        boolean found = false;
        for (Method m : OjsWorkerLifecycle.class.getDeclaredMethods()) {
            if (m.getName().equals("onStop")) {
                found = true;
                assertEquals(1, m.getParameterCount());
                break;
            }
        }
        assertTrue(found, "Should have onStop method");
    }

    @Test
    void hasIsRunningMethod() throws NoSuchMethodException {
        var method = OjsWorkerLifecycle.class.getDeclaredMethod("isRunning");
        assertEquals(boolean.class, method.getReturnType());
    }

    @Test
    void hasWorkerField() throws NoSuchFieldException {
        var field = OjsWorkerLifecycle.class.getDeclaredField("worker");
        assertTrue(field.isAnnotationPresent(jakarta.inject.Inject.class));
        assertEquals(org.openjobspec.ojs.OJSWorker.class, field.getType());
    }

    @Test
    void hasRegistryField() throws NoSuchFieldException {
        var field = OjsWorkerLifecycle.class.getDeclaredField("registry");
        assertTrue(field.isAnnotationPresent(jakarta.inject.Inject.class));
        assertEquals(OjsJobRegistry.class, field.getType());
    }

    @Test
    void hasConfigField() throws NoSuchFieldException {
        var field = OjsWorkerLifecycle.class.getDeclaredField("config");
        assertTrue(field.isAnnotationPresent(jakarta.inject.Inject.class));
        assertEquals(OjsConfig.class, field.getType());
    }

    @Test
    void onStartMethodObservesStartupEvent() throws Exception {
        for (Method m : OjsWorkerLifecycle.class.getDeclaredMethods()) {
            if (m.getName().equals("onStart")) {
                var params = m.getParameters();
                assertTrue(params[0].isAnnotationPresent(
                        jakarta.enterprise.event.Observes.class),
                        "First parameter should have @Observes");
                assertEquals(io.quarkus.runtime.StartupEvent.class,
                        params[0].getType());
                return;
            }
        }
        fail("onStart method not found");
    }

    @Test
    void onStopMethodObservesShutdownEvent() throws Exception {
        for (Method m : OjsWorkerLifecycle.class.getDeclaredMethods()) {
            if (m.getName().equals("onStop")) {
                var params = m.getParameters();
                assertTrue(params[0].isAnnotationPresent(
                        jakarta.enterprise.event.Observes.class),
                        "First parameter should have @Observes");
                assertEquals(io.quarkus.runtime.ShutdownEvent.class,
                        params[0].getType());
                return;
            }
        }
        fail("onStop method not found");
    }
}
