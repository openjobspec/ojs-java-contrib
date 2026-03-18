package org.openjobspec.micronaut;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.runtime.event.ApplicationShutdownEvent;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OjsEventBridgeTest {

    @Mock ApplicationEventPublisher<OjsJobEvent> publisher;
    @Mock ServerStartupEvent startupEvent;
    @Mock ApplicationShutdownEvent shutdownEvent;

    @Test
    void isSingleton() {
        assertTrue(OjsEventBridge.class.isAnnotationPresent(
                jakarta.inject.Singleton.class));
    }

    @Test
    void hasRequiresAnnotation() {
        var requires = OjsEventBridge.class.getAnnotation(
                io.micronaut.context.annotation.Requires.class);
        assertNotNull(requires);
        assertEquals("ojs.events-enabled", requires.property());
        assertEquals("true", requires.value());
    }

    @Test
    void implementsApplicationEventListener() {
        assertTrue(io.micronaut.context.event.ApplicationEventListener.class
                .isAssignableFrom(OjsEventBridge.class));
    }

    @Test
    void subscriptionIsNullBeforeStartup() {
        var config = new OjsConfiguration();
        config.setEventsEnabled(true);

        var bridge = new OjsEventBridge(config, publisher);
        assertNull(bridge.getSubscription());
    }

    @Test
    void onShutdownWithNullSubscriptionDoesNotThrow() {
        var config = new OjsConfiguration();
        var bridge = new OjsEventBridge(config, publisher);

        assertDoesNotThrow(() -> bridge.onShutdown(shutdownEvent));
    }

    @Test
    void shutdownHandlerHasEventListenerAnnotation() throws NoSuchMethodException {
        var method = OjsEventBridge.class.getDeclaredMethod(
                "onShutdown", ApplicationShutdownEvent.class);
        assertTrue(method.isAnnotationPresent(
                io.micronaut.runtime.event.annotation.EventListener.class));
    }

    @Test
    void configChannelDefaultIsWildcard() {
        var config = new OjsConfiguration();
        assertEquals("*", config.getEventsChannel());
    }
}
