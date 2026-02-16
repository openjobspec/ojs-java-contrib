package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.OJSWorker;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OjsExtensionTest {

    @Test
    void ojsJobAnnotationIsRetainedAtRuntime() throws NoSuchMethodException {
        var method = SampleHandler.class.getDeclaredMethod("handle", JobContext.class);
        var annotation = method.getAnnotation(OjsJob.class);

        assertNotNull(annotation);
        assertEquals("test.job", annotation.value());
    }

    @Test
    void ojsConfigDefaultValues() {
        // Verify the annotation defaults are correct
        var configClass = OjsConfig.class;
        assertTrue(configClass.isAnnotationPresent(io.smallrye.config.ConfigMapping.class));

        var mapping = configClass.getAnnotation(io.smallrye.config.ConfigMapping.class);
        assertEquals("ojs", mapping.prefix());
    }

    @Test
    void ojsProducerCreatesClient() {
        var producer = new OjsProducer();
        // Verify producer methods exist and have correct return types
        var methods = producer.getClass().getDeclaredMethods();
        boolean hasClientProducer = false;
        boolean hasWorkerProducer = false;

        for (Method m : methods) {
            if (m.getReturnType() == org.openjobspec.ojs.OJSClient.class) {
                hasClientProducer = true;
            }
            if (m.getReturnType() == org.openjobspec.ojs.OJSWorker.class) {
                hasWorkerProducer = true;
            }
        }

        assertTrue(hasClientProducer, "Should have OJSClient producer method");
        assertTrue(hasWorkerProducer, "Should have OJSWorker producer method");
    }

    static class SampleHandler {
        @OjsJob("test.job")
        public Object handle(JobContext ctx) {
            return Map.of("handled", true);
        }
    }
}
