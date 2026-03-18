package org.openjobspec.micronaut;

import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSWorker;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsWorkerLifecycleTest {

    @Mock OJSWorker worker;
    @Mock ServerStartupEvent startupEvent;
    @Mock ApplicationShutdownEvent shutdownEvent;

    @Test
    void implementsApplicationEventListener() {
        assertTrue(io.micronaut.context.event.ApplicationEventListener.class
                .isAssignableFrom(OjsWorkerLifecycle.class));
    }

    @Test
    void isSingleton() {
        assertTrue(OjsWorkerLifecycle.class.isAnnotationPresent(
                jakarta.inject.Singleton.class));
    }

    @Test
    void hasRequiresAnnotation() {
        assertTrue(OjsWorkerLifecycle.class.isAnnotationPresent(
                io.micronaut.context.annotation.Requires.class));
    }

    @Test
    void onStartupRegistersMiddlewareAndStartsWorker() throws InterruptedException {
        Middleware mw = mock(Middleware.class);
        var lifecycle = new OjsWorkerLifecycle(worker, List.of(mw));

        lifecycle.onApplicationEvent(startupEvent);

        // Give the virtual thread time to invoke start()
        Thread.sleep(100);

        verify(worker).use(mw);
        verify(worker).start();
    }

    @Test
    void onStartupWithNoMiddleware() throws InterruptedException {
        var lifecycle = new OjsWorkerLifecycle(worker, List.of());

        lifecycle.onApplicationEvent(startupEvent);
        Thread.sleep(100);

        verify(worker, never()).use(any(Middleware.class));
        verify(worker).start();
    }

    @Test
    void onStartupWithNullMiddlewareList() throws InterruptedException {
        var lifecycle = new OjsWorkerLifecycle(worker, null);

        lifecycle.onApplicationEvent(startupEvent);
        Thread.sleep(100);

        verify(worker).start();
    }

    @Test
    void onShutdownStopsWorker() {
        var lifecycle = new OjsWorkerLifecycle(worker, List.of());

        lifecycle.onShutdown(shutdownEvent);

        verify(worker).stop();
    }

    @Test
    void onStartupRegistersMultipleMiddlewares() throws InterruptedException {
        Middleware mw1 = mock(Middleware.class);
        Middleware mw2 = mock(Middleware.class);
        Middleware mw3 = mock(Middleware.class);
        var lifecycle = new OjsWorkerLifecycle(worker, List.of(mw1, mw2, mw3));

        lifecycle.onApplicationEvent(startupEvent);
        Thread.sleep(100);

        verify(worker).use(mw1);
        verify(worker).use(mw2);
        verify(worker).use(mw3);
        verify(worker).start();
    }

    @Test
    void shutdownHandlerHasEventListenerAnnotation() throws NoSuchMethodException {
        var method = OjsWorkerLifecycle.class.getDeclaredMethod(
                "onShutdown", ApplicationShutdownEvent.class);
        assertTrue(method.isAnnotationPresent(
                io.micronaut.runtime.event.annotation.EventListener.class));
    }
}
