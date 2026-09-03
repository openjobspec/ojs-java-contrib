package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.JobContext;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReflectiveJobHandlerTest {

    @Test
    void invokesMethodAndReturnsResult() throws Exception {
        Method m = Handlers.class.getDeclaredMethod("ok", JobContext.class);
        var handler = new ReflectiveJobHandler(new Handlers(), m);

        Object result = handler.handle(mock(JobContext.class));

        assertEquals("done", result);
    }

    @Test
    void unwrapsCheckedExceptionFromHandler() throws Exception {
        Method m = Handlers.class.getDeclaredMethod("throwsChecked", JobContext.class);
        var handler = new ReflectiveJobHandler(new Handlers(), m);

        var ex = assertThrows(IOException.class, () -> handler.handle(mock(JobContext.class)));
        assertEquals("boom", ex.getMessage());
    }

    @Test
    void wrapsNonExceptionThrowableInRuntimeException() throws Exception {
        Method m = Handlers.class.getDeclaredMethod("throwsError", JobContext.class);
        var handler = new ReflectiveJobHandler(new Handlers(), m);

        var ex = assertThrows(RuntimeException.class, () -> handler.handle(mock(JobContext.class)));
        assertTrue(ex.getMessage().contains("non-exception throwable"));
        assertInstanceOf(AssertionError.class, ex.getCause());
    }

    @Test
    void invokesInaccessiblePrivateMethod() throws Exception {
        Method m = Handlers.class.getDeclaredMethod("privateOk", JobContext.class);
        var handler = new ReflectiveJobHandler(new Handlers(), m);

        Object result = handler.handle(mock(JobContext.class));

        assertEquals("private-done", result);
    }

    static class Handlers {
        public Object ok(JobContext ctx) {
            return "done";
        }

        public Object throwsChecked(JobContext ctx) throws IOException {
            throw new IOException("boom");
        }

        public Object throwsError(JobContext ctx) {
            throw new AssertionError("fatal");
        }

        private Object privateOk(JobContext ctx) {
            return "private-done";
        }
    }
}
