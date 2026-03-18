package org.openjobspec.quarkus;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.Middleware;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class OjsEncryptionProducerTest {

    @Test
    void isApplicationScoped() {
        assertTrue(OjsEncryptionProducer.class.isAnnotationPresent(
                jakarta.enterprise.context.ApplicationScoped.class));
    }

    @Test
    void encryptionMiddlewareMethodHasProducesAnnotation() throws NoSuchMethodException {
        var method = OjsEncryptionProducer.class.getDeclaredMethod(
                "encryptionMiddleware", OjsConfig.class);
        assertTrue(method.isAnnotationPresent(jakarta.enterprise.inject.Produces.class));
        assertTrue(method.isAnnotationPresent(jakarta.inject.Singleton.class));
        assertTrue(method.isAnnotationPresent(jakarta.inject.Named.class));
        assertEquals("ojsEncryption", method.getAnnotation(jakarta.inject.Named.class).value());
    }

    @Test
    void decryptionMiddlewareMethodHasProducesAnnotation() throws NoSuchMethodException {
        var method = OjsEncryptionProducer.class.getDeclaredMethod(
                "decryptionMiddleware", OjsConfig.class);
        assertTrue(method.isAnnotationPresent(jakarta.enterprise.inject.Produces.class));
        assertTrue(method.isAnnotationPresent(jakarta.inject.Singleton.class));
        assertTrue(method.isAnnotationPresent(jakarta.inject.Named.class));
        assertEquals("ojsDecryption", method.getAnnotation(jakarta.inject.Named.class).value());
    }

    @Test
    void encryptionMethodReturnsMiddleware() throws NoSuchMethodException {
        var method = OjsEncryptionProducer.class.getDeclaredMethod(
                "encryptionMiddleware", OjsConfig.class);
        assertEquals(Middleware.class, method.getReturnType());
    }

    @Test
    void decryptionMethodReturnsMiddleware() throws NoSuchMethodException {
        var method = OjsEncryptionProducer.class.getDeclaredMethod(
                "decryptionMiddleware", OjsConfig.class);
        assertEquals(Middleware.class, method.getReturnType());
    }

    @Test
    void canBeInstantiated() {
        var producer = new OjsEncryptionProducer();
        assertNotNull(producer);
    }

    @Test
    void hasTwoProducerMethods() {
        int producerCount = 0;
        for (Method m : OjsEncryptionProducer.class.getDeclaredMethods()) {
            if (m.isAnnotationPresent(jakarta.enterprise.inject.Produces.class)) {
                producerCount++;
            }
        }
        assertEquals(2, producerCount,
                "Should have exactly 2 @Produces methods (encryption + decryption)");
    }
}
