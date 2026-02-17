package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.JobHandler;
import org.openjobspec.ojs.OJSWorker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsJobRegistrarTest {

    @Mock
    OJSWorker worker;

    @Test
    void registersMethodLevelAnnotation() {
        var registrar = new OjsJobRegistrar(worker);
        var bean = new MethodLevelBean();

        registrar.postProcessAfterInitialization(bean, "methodLevelBean");

        verify(worker).register(eq("test.method"), any(JobHandler.class));
    }

    @Test
    void registersClassLevelAnnotationWithHandler() {
        var registrar = new OjsJobRegistrar(worker);
        var bean = new ClassLevelBean();

        registrar.postProcessAfterInitialization(bean, "classLevelBean");

        verify(worker).register(eq("test.class"), any(JobHandler.class));
    }

    @Test
    void resolveJobTypeFromValue() {
        var type = OjsJobRegistrar.resolveJobType(MethodLevelBean.class.getDeclaredMethods()[0].getAnnotation(OjsJob.class));
        assertEquals("test.method", type);
    }

    @Test
    void ignoresBeansWithoutAnnotation() {
        var registrar = new OjsJobRegistrar(worker);
        var bean = new PlainBean();

        registrar.postProcessAfterInitialization(bean, "plainBean");

        verify(worker, never()).register(any(), any());
    }

    @Test
    void returnsSameBeanInstance() {
        var registrar = new OjsJobRegistrar(worker);
        var bean = new PlainBean();

        var result = registrar.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    // --- Test beans ---

    static class MethodLevelBean {
        @OjsJob("test.method")
        public Object handle(org.openjobspec.ojs.JobContext ctx) {
            return null;
        }
    }

    @OjsJob(type = "test.class")
    static class ClassLevelBean implements OjsJobHandler {
        @Override
        public Object execute(OjsJobContext ctx) {
            return null;
        }
    }

    static class PlainBean {
        public void doSomething() {}
    }
}
