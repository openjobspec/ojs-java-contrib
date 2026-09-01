package org.openjobspec.spring;

import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.JobHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Adapts a {@link OjsJob @OjsJob}-annotated method on a Spring bean to the SDK
 * {@link JobHandler} contract.
 *
 * <p>Owns the reflection mechanics of method-level handler invocation so that
 * {@link OjsJobRegistrar} is concerned only with scanning and registration:
 * <ul>
 *   <li>ensures the target method is accessible before invocation;</li>
 *   <li>unwraps {@link InvocationTargetException} so the handler's own checked
 *       exception propagates to the worker unchanged, and wraps a non-{@link Exception}
 *       {@link Throwable} cause in a {@link RuntimeException}.</li>
 * </ul>
 */
final class ReflectiveJobHandler implements JobHandler {

    private final Object bean;
    private final Method method;

    ReflectiveJobHandler(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    @Override
    public Object handle(JobContext ctx) throws Exception {
        try {
            if (!method.canAccess(bean)) {
                method.setAccessible(true);
            }
            return method.invoke(bean, ctx);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception ex) {
                throw ex;
            }
            throw new RuntimeException("Job handler threw non-exception throwable", e.getCause());
        }
    }
}
