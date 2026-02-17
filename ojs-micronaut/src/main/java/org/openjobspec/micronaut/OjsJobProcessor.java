package org.openjobspec.micronaut;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.OJSWorker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Listens for bean creation events and registers methods annotated with
 * {@link OjsJob} as handlers on the {@link OJSWorker}.
 */
@Singleton
public class OjsJobProcessor implements BeanCreatedEventListener<Object> {

    private final OJSWorker worker;

    public OjsJobProcessor(OJSWorker worker) {
        this.worker = worker;
    }

    @Override
    public Object onCreated(BeanCreatedEvent<Object> event) {
        var bean = event.getBean();
        if (bean instanceof OJSWorker || bean instanceof OjsJobProcessor) {
            return bean;
        }
        for (Method method : bean.getClass().getDeclaredMethods()) {
            var annotation = method.getAnnotation(OjsJob.class);
            if (annotation != null) {
                String jobType = annotation.value();
                worker.register(jobType, ctx -> invokeHandler(bean, method, ctx));
            }
        }
        return bean;
    }

    private Object invokeHandler(Object bean, Method method, JobContext ctx) throws Exception {
        try {
            if (!method.canAccess(bean)) {
                method.setAccessible(true);
            }
            return method.invoke(bean, ctx);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof Exception ex) throw ex;
            throw new RuntimeException("Job handler threw non-exception throwable", e.getCause());
        }
    }
}
