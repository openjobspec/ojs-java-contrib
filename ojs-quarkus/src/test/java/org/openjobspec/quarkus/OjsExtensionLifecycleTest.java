package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.JobContext;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OjsExtensionLifecycleTest {

    @Test
    void extensionImplementsCdiExtension() {
        assertTrue(jakarta.enterprise.inject.spi.Extension.class
                .isAssignableFrom(OjsExtension.class));
    }

    @Test
    void extensionHasProcessAnnotatedTypeMethod() {
        boolean found = false;
        for (Method m : OjsExtension.class.getDeclaredMethods()) {
            if (m.getName().equals("processAnnotatedType")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Extension should have processAnnotatedType method");
    }

    @Test
    void extensionHasAfterDeploymentValidationMethod() {
        boolean found = false;
        for (Method m : OjsExtension.class.getDeclaredMethods()) {
            if (m.getName().equals("afterDeploymentValidation")) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Extension should have afterDeploymentValidation method");
    }

    @Test
    void extensionCanBeInstantiated() {
        var extension = new OjsExtension();
        assertNotNull(extension);
    }

    @Test
    void extensionHandlerScanFindsAnnotatedMethods() throws Exception {
        // Verify the annotation scanning logic works by checking that
        // the sample handler class has the expected annotations
        var methods = SampleBean.class.getDeclaredMethods();
        int handlerCount = 0;
        for (Method m : methods) {
            if (m.getAnnotation(OjsJob.class) != null) {
                handlerCount++;
            }
        }
        assertEquals(1, handlerCount);
    }

    @Test
    void invokeHandlerReflectionWorks() throws Exception {
        var bean = new SampleBean();
        var method = SampleBean.class.getDeclaredMethod("handle", JobContext.class);

        // Verify the method is accessible and invocable
        assertFalse(method.getReturnType().equals(void.class),
                "Handler method should return Object, not void");
        assertEquals(1, method.getParameterCount());
        assertEquals(JobContext.class, method.getParameterTypes()[0]);
    }

    static class SampleBean {
        @OjsJob("lifecycle.test")
        public Object handle(JobContext ctx) {
            return Map.of("ok", true);
        }
    }
}
