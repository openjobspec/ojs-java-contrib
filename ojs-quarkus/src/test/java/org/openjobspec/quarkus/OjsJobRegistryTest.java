package org.openjobspec.quarkus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.JobContext;
import org.openjobspec.ojs.JobHandler;

import static org.junit.jupiter.api.Assertions.*;

class OjsJobRegistryTest {

    private OjsJobRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new OjsJobRegistry();
    }

    @Test
    void isApplicationScoped() {
        assertTrue(OjsJobRegistry.class.isAnnotationPresent(
                jakarta.enterprise.context.ApplicationScoped.class));
    }

    @Test
    void registerAndRetrieveHandler() {
        JobHandler handler = ctx -> "done";
        registry.register("email.send", handler);

        assertTrue(registry.hasHandler("email.send"));
        assertEquals(1, registry.size());
        assertSame(handler, registry.getHandlers().get("email.send"));
    }

    @Test
    void registerMultipleHandlers() {
        registry.register("type.a", ctx -> "a");
        registry.register("type.b", ctx -> "b");

        assertEquals(2, registry.size());
        assertTrue(registry.hasHandler("type.a"));
        assertTrue(registry.hasHandler("type.b"));
    }

    @Test
    void hasHandlerReturnsFalseForUnknown() {
        assertFalse(registry.hasHandler("unknown.type"));
    }

    @Test
    void getHandlersReturnsUnmodifiableMap() {
        registry.register("test", ctx -> null);
        var handlers = registry.getHandlers();

        assertThrows(UnsupportedOperationException.class,
                () -> handlers.put("another", ctx -> null));
    }

    @Test
    void registerRejectsNullJobType() {
        assertThrows(IllegalArgumentException.class,
                () -> registry.register(null, ctx -> null));
    }

    @Test
    void registerRejectsBlankJobType() {
        assertThrows(IllegalArgumentException.class,
                () -> registry.register("  ", ctx -> null));
    }

    @Test
    void registerRejectsNullHandler() {
        assertThrows(IllegalArgumentException.class,
                () -> registry.register("test.type", null));
    }

    @Test
    void registerOverwritesExistingHandler() {
        JobHandler first = ctx -> "first";
        JobHandler second = ctx -> "second";

        registry.register("overwrite.test", first);
        registry.register("overwrite.test", second);

        assertEquals(1, registry.size());
        assertSame(second, registry.getHandlers().get("overwrite.test"));
    }

    @Test
    void emptyRegistryHasZeroSize() {
        assertEquals(0, registry.size());
        assertTrue(registry.getHandlers().isEmpty());
    }
}
