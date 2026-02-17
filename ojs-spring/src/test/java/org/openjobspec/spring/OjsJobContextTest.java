package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.Job;
import org.openjobspec.ojs.JobContext;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsJobContextTest {

    @Test
    void wrapsJobContext() {
        var job = createTestJob("test-id", "email.send", "emails");
        var sdkCtx = mock(JobContext.class);
        when(sdkCtx.job()).thenReturn(job);
        when(sdkCtx.attempt()).thenReturn(1);
        when(sdkCtx.queue()).thenReturn("emails");

        var ctx = new OjsJobContext(sdkCtx);

        assertEquals("email.send", ctx.jobType());
        assertEquals("test-id", ctx.jobId());
        assertEquals("emails", ctx.queue());
        assertEquals(1, ctx.attempt());
        assertSame(sdkCtx, ctx.unwrap());
    }

    @Test
    void delegatesJobMethods() {
        var job = createTestJob("job-1", "report.generate", "default");
        var sdkCtx = mock(JobContext.class);
        when(sdkCtx.job()).thenReturn(job);
        when(sdkCtx.isCancelled()).thenReturn(false);

        var ctx = new OjsJobContext(sdkCtx);

        assertSame(job, ctx.job());
        assertEquals(List.of(), ctx.args());
        assertEquals(Map.of(), ctx.argsMap());
        assertEquals(Map.of(), ctx.meta());
        assertFalse(ctx.isCancelled());
    }

    @Test
    void setResultDelegatesToSdkContext() {
        var sdkCtx = mock(JobContext.class);
        var ctx = new OjsJobContext(sdkCtx);

        ctx.setResult(Map.of("done", true));
        verify(sdkCtx).setResult(Map.of("done", true));
    }

    @Test
    void heartbeatDelegatesToSdkContext() {
        var sdkCtx = mock(JobContext.class);
        var ctx = new OjsJobContext(sdkCtx);

        ctx.heartbeat();
        verify(sdkCtx).heartbeat();
    }

    private static Job createTestJob(String id, String type, String queue) {
        return new Job(Job.SPEC_VERSION, id, type, queue,
                List.of(), Map.of(), 0, 0, null, null, null, null, null,
                "active", 1, null, null, null, null, null, null, List.of());
    }
}
