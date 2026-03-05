package org.openjobspec.micronaut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.OJSWorker;

import io.micronaut.context.event.BeanCreatedEvent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsJobProcessorTest {

    @Mock
    OJSWorker worker;

    @Test
    void processorIsSingleton() {
        assertTrue(OjsJobProcessor.class.isAnnotationPresent(
                jakarta.inject.Singleton.class));
    }

    @Test
    void implementsBeanCreatedEventListener() {
        assertTrue(io.micronaut.context.event.BeanCreatedEventListener.class
                .isAssignableFrom(OjsJobProcessor.class));
    }

    @Test
    void skipsWorkerBeanRegistration() {
        var processor = new OjsJobProcessor(worker);

        @SuppressWarnings("unchecked")
        var event = mock(BeanCreatedEvent.class);
        when(event.getBean()).thenReturn(worker);

        var result = processor.onCreated(event);
        assertSame(worker, result);
        verify(worker, never()).register(anyString(), any());
    }

    @Test
    void skipsProcessorBeanRegistration() {
        var processor = new OjsJobProcessor(worker);

        @SuppressWarnings("unchecked")
        var event = mock(BeanCreatedEvent.class);
        when(event.getBean()).thenReturn(processor);

        var result = processor.onCreated(event);
        assertSame(processor, result);
        verify(worker, never()).register(anyString(), any());
    }

    @Test
    void registersAnnotatedMethodsAsHandlers() {
        var processor = new OjsJobProcessor(worker);
        var bean = new AnnotatedBean();

        @SuppressWarnings("unchecked")
        var event = mock(BeanCreatedEvent.class);
        when(event.getBean()).thenReturn(bean);

        processor.onCreated(event);

        verify(worker).register(eq("test.process"), any());
    }

    @Test
    void returnsOriginalBean() {
        var processor = new OjsJobProcessor(worker);
        var bean = new AnnotatedBean();

        @SuppressWarnings("unchecked")
        var event = mock(BeanCreatedEvent.class);
        when(event.getBean()).thenReturn(bean);

        var result = processor.onCreated(event);
        assertSame(bean, result);
    }

    @Test
    void ignoresBeansWithoutAnnotation() {
        var processor = new OjsJobProcessor(worker);
        var bean = new PlainBean();

        @SuppressWarnings("unchecked")
        var event = mock(BeanCreatedEvent.class);
        when(event.getBean()).thenReturn(bean);

        processor.onCreated(event);

        verify(worker, never()).register(anyString(), any());
    }

    @Test
    void registersMultipleHandlersFromSameBean() {
        var processor = new OjsJobProcessor(worker);
        var bean = new MultiHandlerBean();

        @SuppressWarnings("unchecked")
        var event = mock(BeanCreatedEvent.class);
        when(event.getBean()).thenReturn(bean);

        processor.onCreated(event);

        verify(worker).register(eq("type.one"), any());
        verify(worker).register(eq("type.two"), any());
    }

    static class AnnotatedBean {
        @OjsJob("test.process")
        public Object handle(JobContext ctx) {
            return Map.of("done", true);
        }
    }

    static class PlainBean {
        public void doSomething() {}
    }

    static class MultiHandlerBean {
        @OjsJob("type.one")
        public Object handleOne(JobContext ctx) { return null; }

        @OjsJob("type.two")
        public Object handleTwo(JobContext ctx) { return null; }
    }
}

