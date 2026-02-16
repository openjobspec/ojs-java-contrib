package org.openjobspec.quarkus;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;
import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.OJSWorker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * CDI extension that scans for {@link OjsJob}-annotated methods and registers
 * them as handlers on the {@link OJSWorker}.
 */
public class OjsExtension implements Extension {

    private record HandlerInfo(Class<?> beanClass, Method method, String jobType) {}

    private final List<HandlerInfo> handlers = new ArrayList<>();

    <T> void processAnnotatedType(@Observes @WithAnnotations(OjsJob.class) ProcessAnnotatedType<T> pat) {
        var javaClass = pat.getAnnotatedType().getJavaClass();
        for (Method method : javaClass.getDeclaredMethods()) {
            var annotation = method.getAnnotation(OjsJob.class);
            if (annotation != null) {
                handlers.add(new HandlerInfo(javaClass, method, annotation.value()));
            }
        }
    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        if (handlers.isEmpty()) return;

        var workerInstance = beanManager.createInstance().select(OJSWorker.class);
        if (!workerInstance.isResolvable()) return;

        var worker = workerInstance.get();
        for (var handler : handlers) {
            var beanInstance = beanManager.createInstance().select(handler.beanClass());
            if (beanInstance.isResolvable()) {
                var bean = beanInstance.get();
                worker.register(handler.jobType(), ctx -> invokeHandler(bean, handler.method(), ctx));
            }
        }
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
