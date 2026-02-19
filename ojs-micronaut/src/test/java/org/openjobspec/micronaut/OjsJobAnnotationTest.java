package org.openjobspec.micronaut;

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
        var retention = OjsJob.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
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
        var method = SampleJob.class.getDeclaredMethod("handle", JobContext.class);
        var annotation = method.getAnnotation(OjsJob.class);
        assertEquals("email.send", annotation.value());
    }

    @Test
    void differentMethodsCanHaveDifferentJobTypes() throws NoSuchMethodException {
        var m1 = MultiJob.class.getDeclaredMethod("handleA", JobContext.class);
        var m2 = MultiJob.class.getDeclaredMethod("handleB", JobContext.class);

        assertEquals("job.a", m1.getAnnotation(OjsJob.class).value());
        assertEquals("job.b", m2.getAnnotation(OjsJob.class).value());
    }

    static class SampleJob {
        @OjsJob("email.send")
        public Object handle(JobContext ctx) {
            return Map.of("sent", true);
        }
    }

    static class MultiJob {
        @OjsJob("job.a")
        public Object handleA(JobContext ctx) { return null; }

        @OjsJob("job.b")
        public Object handleB(JobContext ctx) { return null; }
    }
}
