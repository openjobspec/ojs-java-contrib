package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OjsEventBridgeTest {

    @Mock
    ApplicationEventPublisher publisher;

    @Test
    void constructorRejectsNullServerUrl() {
        assertThrows(NullPointerException.class,
                () -> new OjsEventBridge(null, publisher));
    }

    @Test
    void constructorRejectsNullPublisher() {
        assertThrows(NullPointerException.class,
                () -> new OjsEventBridge("http://localhost:8080", null));
    }

    @Test
    void cancelAllClearsSubscriptions() {
        var bridge = new OjsEventBridge("http://localhost:8080", publisher);
        assertEquals(0, bridge.activeSubscriptionCount());

        bridge.cancelAll();
        assertEquals(0, bridge.activeSubscriptionCount());
    }

    @Test
    void activeSubscriptionCountStartsAtZero() {
        var bridge = new OjsEventBridge("http://localhost:8080", publisher);
        assertEquals(0, bridge.activeSubscriptionCount());
    }
}
