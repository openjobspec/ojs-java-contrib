package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OjsProducerTest {

    @Test
    void producerIsApplicationScoped() {
        assertTrue(OjsProducer.class.isAnnotationPresent(
                jakarta.enterprise.context.ApplicationScoped.class));
    }

    @Test
    void ojsClientMethodHasProducesAnnotation() throws NoSuchMethodException {
        Method method = OjsProducer.class.getDeclaredMethod("ojsClient", OjsConfig.class);
        assertTrue(method.isAnnotationPresent(jakarta.enterprise.inject.Produces.class));
        assertTrue(method.isAnnotationPresent(jakarta.inject.Singleton.class));
    }

    @Test
    void ojsWorkerMethodHasProducesAnnotation() throws NoSuchMethodException {
        Method method = OjsProducer.class.getDeclaredMethod("ojsWorker", OjsConfig.class);
        assertTrue(method.isAnnotationPresent(jakarta.enterprise.inject.Produces.class));
        assertTrue(method.isAnnotationPresent(jakarta.inject.Singleton.class));
    }

    @Test
    void ojsClientMethodReturnsCorrectType() throws NoSuchMethodException {
        Method method = OjsProducer.class.getDeclaredMethod("ojsClient", OjsConfig.class);
        assertEquals(OJSClient.class, method.getReturnType());
    }

    @Test
    void ojsWorkerMethodReturnsCorrectType() throws NoSuchMethodException {
        Method method = OjsProducer.class.getDeclaredMethod("ojsWorker", OjsConfig.class);
        assertEquals(OJSWorker.class, method.getReturnType());
    }

    @Test
    void producerCreatesClientFromConfig(@Mock OjsConfig config) {
        when(config.url()).thenReturn("http://test:9090");

        var producer = new OjsProducer();
        var client = producer.ojsClient(config);

        assertNotNull(client);
        assertInstanceOf(OJSClient.class, client);
    }

    @Test
    void producerCreatesWorkerFromConfig(@Mock OjsConfig config) {
        when(config.url()).thenReturn("http://test:9090");
        when(config.queues()).thenReturn(List.of("high", "low"));
        when(config.concurrency()).thenReturn(20);

        var producer = new OjsProducer();
        var worker = producer.ojsWorker(config);

        assertNotNull(worker);
        assertInstanceOf(OJSWorker.class, worker);
    }
}
