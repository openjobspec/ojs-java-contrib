package org.openjobspec.spring;

import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.OJSWorker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * Scans Spring beans for methods annotated with {@link OjsJob} and registers
 * them as job handlers on the {@link OJSWorker}.
 */
public class OjsJobRegistrar implements BeanPostProcessor {

    private final OJSWorker worker;

    public OjsJobRegistrar(OJSWorker worker) {
        this.worker = worker;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
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
