package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.JobContext;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OjsJobAnnotationTest {

    @Test
    void annotationIsRetainedAtRuntime() {
        assertTrue(OjsJob.class.isAnnotationPresent(Retention.class));
        assertEquals(RetentionPolicy.RUNTIME,
                OjsJob.class.getAnnotation(Retention.class).value());
    }

    @Test
    void annotationTargetsMethodsOnly() {
        var target = OjsJob.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.METHOD}, target.value());
    }

    @Test
    void annotationIsDocumented() {
        assertTrue(OjsJob.class.isAnnotationPresent(Documented.class));
    }

    @Test
    void annotationValueReturnsJobType() throws NoSuchMethodException {
        var method = SampleJob.class.getDeclaredMethod("process", JobContext.class);
        var annotation = method.getAnnotation(OjsJob.class);
        assertEquals("order.process", annotation.value());
    }

    @Test
    void multipleHandlersCanHaveDifferentTypes() throws NoSuchMethodException {
        var m1 = MultiHandlerBean.class.getDeclaredMethod("handleA", JobContext.class);
        var m2 = MultiHandlerBean.class.getDeclaredMethod("handleB", JobContext.class);

        assertEquals("type.a", m1.getAnnotation(OjsJob.class).value());
        assertEquals("type.b", m2.getAnnotation(OjsJob.class).value());
    }

    static class SampleJob {
        @OjsJob("order.process")
        public Object process(JobContext ctx) {
            return Map.of("processed", true);
        }
    }

    static class MultiHandlerBean {
        @OjsJob("type.a")
        public Object handleA(JobContext ctx) { return null; }

        @OjsJob("type.b")
        public Object handleB(JobContext ctx) { return null; }
    }
}
