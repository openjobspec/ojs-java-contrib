package org.openjobspec.micronaut;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.JobContext;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OjsFactoryTest {

    @Test
    void ojsJobAnnotationIsRetainedAtRuntime() throws NoSuchMethodException {
        var method = SampleHandler.class.getDeclaredMethod("handle", JobContext.class);
        var annotation = method.getAnnotation(OjsJob.class);

        assertNotNull(annotation);
        assertEquals("test.job", annotation.value());
    }

    @Test
    void configurationHasCorrectDefaults() {
        var config = new OjsConfiguration();

        assertEquals("http://localhost:8080", config.getUrl());
        assertEquals(1, config.getQueues().size());
        assertEquals("default", config.getQueues().getFirst());
        assertEquals(10, config.getConcurrency());
    }

    @Test
    void configurationIsSettable() {
        var config = new OjsConfiguration();
        config.setUrl("http://custom:9090");
        config.setQueues(java.util.List.of("high", "low"));
        config.setConcurrency(20);

        assertEquals("http://custom:9090", config.getUrl());
        assertEquals(2, config.getQueues().size());
        assertEquals(20, config.getConcurrency());
    }

    @Test
    void factoryMethodsExist() {
        var factory = new OjsFactory();
        var methods = factory.getClass().getDeclaredMethods();
        boolean hasClientFactory = false;
        boolean hasWorkerFactory = false;

        for (Method m : methods) {
            if (m.getReturnType() == org.openjobspec.ojs.OJSClient.class) {
                hasClientFactory = true;
            }
            if (m.getReturnType() == org.openjobspec.ojs.OJSWorker.class) {
                hasWorkerFactory = true;
            }
        }

        assertTrue(hasClientFactory, "Should have OJSClient factory method");
        assertTrue(hasWorkerFactory, "Should have OJSWorker factory method");
    }

    static class SampleHandler {
        @OjsJob("test.job")
        public Object handle(JobContext ctx) {
            return Map.of("handled", true);
        }
    }
}
