package org.openjobspec.micronaut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSWorker;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsMiddlewareChainTest {

    @Mock OJSWorker worker;

    @Test
    void isSingleton() {
        assertTrue(OjsMiddlewareChain.class.isAnnotationPresent(
                jakarta.inject.Singleton.class));
    }

    @Test
    void constructsWithMiddlewareList() {
        Middleware mw = mock(Middleware.class);
        var chain = new OjsMiddlewareChain(List.of(mw));

        assertEquals(1, chain.size());
    }

    @Test
    void constructsWithNullList() {
        var chain = new OjsMiddlewareChain(null);

        assertEquals(0, chain.size());
    }

    @Test
    void constructsWithEmptyList() {
        var chain = new OjsMiddlewareChain(List.of());

        assertEquals(0, chain.size());
    }

    @Test
    void addIncreasesSize() {
        var chain = new OjsMiddlewareChain(new ArrayList<>());
        Middleware mw = mock(Middleware.class);

        chain.add(mw);

        assertEquals(1, chain.size());
    }

    @Test
    void addNamedIncreasesSize() {
        var chain = new OjsMiddlewareChain(new ArrayList<>());
        Middleware mw = mock(Middleware.class);

        chain.add("logging", mw);

        assertEquals(1, chain.size());
    }

    @Test
    void getMiddlewaresReturnsUnmodifiableView() {
        Middleware mw = mock(Middleware.class);
        var chain = new OjsMiddlewareChain(new ArrayList<>(List.of(mw)));

        var middlewares = chain.getMiddlewares();

        assertThrows(UnsupportedOperationException.class, () ->
                middlewares.add(mock(Middleware.class)));
    }

    @Test
    void applyToRegistersAllMiddlewareOnWorker() {
        Middleware mw1 = mock(Middleware.class);
        Middleware mw2 = mock(Middleware.class);
        var chain = new OjsMiddlewareChain(new ArrayList<>(List.of(mw1, mw2)));

        chain.applyTo(worker);

        verify(worker).use(mw1);
        verify(worker).use(mw2);
    }

    @Test
    void applyToWithEmptyChainDoesNothing() {
        var chain = new OjsMiddlewareChain(List.of());

        chain.applyTo(worker);

        verify(worker, never()).use(any(Middleware.class));
    }

    @Test
    void addedMiddlewareAppearsInGetMiddlewares() {
        var chain = new OjsMiddlewareChain(new ArrayList<>());
        Middleware mw = mock(Middleware.class);
        chain.add(mw);

        assertTrue(chain.getMiddlewares().contains(mw));
    }
}
