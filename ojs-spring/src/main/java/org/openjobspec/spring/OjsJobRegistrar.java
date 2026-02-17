package org.openjobspec.spring;

import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.OJSWorker;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * Scans Spring beans for {@link OjsJob} annotations and {@link OjsJobHandler}
 * implementations, then registers them as job handlers on the {@link OJSWorker}.
 *
 * <p>Supports two registration patterns:
 * <ul>
 *   <li><b>Class-level:</b> Classes annotated with {@code @OjsJob} that implement
 *       {@link OjsJobHandler} are registered using their {@code execute()} method.</li>
 *   <li><b>Method-level:</b> Methods annotated with {@code @OjsJob} on any bean
 *       are registered as individual handlers.</li>
 * </ul>
 */
public class OjsJobRegistrar implements BeanPostProcessor {

    private final OJSWorker worker;

    public OjsJobRegistrar(OJSWorker worker) {
        this.worker = worker;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // Class-level @OjsJob + OjsJobHandler interface
        var classAnnotation = beanClass.getAnnotation(OjsJob.class);
        if (classAnnotation != null && bean instanceof OjsJobHandler handler) {
            String jobType = resolveJobType(classAnnotation);
            if (!jobType.isEmpty()) {
                worker.register(jobType, ctx -> handler.execute(new OjsJobContext(ctx)));
            }
        }

        // Method-level @OjsJob
        for (Method method : beanClass.getDeclaredMethods()) {
            var annotation = method.getAnnotation(OjsJob.class);
            if (annotation != null) {
                String jobType = resolveJobType(annotation);
                worker.register(jobType, ctx -> invokeHandler(bean, method, ctx));
            }
        }
        return bean;
    }

    static String resolveJobType(OjsJob annotation) {
        String type = annotation.type();
        if (type == null || type.isEmpty()) {
            type = annotation.value();
        }
        return type != null ? type : "";
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
