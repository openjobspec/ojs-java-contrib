package org.openjobspec.micronaut;

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
class OjsFactoryBeanCreationTest {

    @Test
    void factoryHasFactoryAnnotation() {
        assertTrue(OjsFactory.class.isAnnotationPresent(
                io.micronaut.context.annotation.Factory.class));
    }

    @Test
    void ojsClientMethodHasSingletonAnnotation() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod("ojsClient", OjsConfiguration.class);
        assertTrue(method.isAnnotationPresent(jakarta.inject.Singleton.class));
    }

    @Test
    void ojsWorkerMethodHasSingletonAnnotation() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod("ojsWorker", OjsConfiguration.class);
        assertTrue(method.isAnnotationPresent(jakarta.inject.Singleton.class));
    }

    @Test
    void factoryCreatesClientFromConfiguration() {
        var config = new OjsConfiguration();
        config.setUrl("http://custom:9999");

        var factory = new OjsFactory();
        var client = factory.ojsClient(config);

        assertNotNull(client);
        assertInstanceOf(OJSClient.class, client);
    }

    @Test
    void factoryCreatesWorkerFromConfiguration() {
        var config = new OjsConfiguration();
        config.setUrl("http://custom:9999");
        config.setQueues(List.of("priority", "batch"));
        config.setConcurrency(32);

        var factory = new OjsFactory();
        var worker = factory.ojsWorker(config);

        assertNotNull(worker);
        assertInstanceOf(OJSWorker.class, worker);
    }

    @Test
    void factoryCreatesClientWithDefaultConfig() {
        var config = new OjsConfiguration();
        var factory = new OjsFactory();
        var client = factory.ojsClient(config);

        assertNotNull(client);
    }

    @Test
    void factoryCreatesWorkerWithDefaultConfig() {
        var config = new OjsConfiguration();
        var factory = new OjsFactory();
        var worker = factory.ojsWorker(config);

        assertNotNull(worker);
    }
}
