package org.openjobspec.micronaut;

import org.junit.jupiter.api.Test;
import org.openjobspec.ojs.EncryptionMiddleware;
import org.openjobspec.ojs.Middleware;

import java.lang.reflect.Method;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class OjsEncryptionFactoryTest {

    @Test
    void factoryHasFactoryAnnotation() {
        assertTrue(OjsFactory.class.isAnnotationPresent(
                io.micronaut.context.annotation.Factory.class));
    }

    @Test
    void encryptionCodecMethodExists() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod("encryptionCodec");
        assertNotNull(method);
        assertEquals(EncryptionMiddleware.EncryptionCodec.class, method.getReturnType());
    }

    @Test
    void encryptionCodecHasSingletonAnnotation() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod("encryptionCodec");
        assertTrue(method.isAnnotationPresent(jakarta.inject.Singleton.class));
    }

    @Test
    void encryptionCodecHasRequiresAnnotation() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod("encryptionCodec");
        var requires = method.getAnnotation(io.micronaut.context.annotation.Requires.class);
        assertNotNull(requires);
        assertEquals("ojs.encryption-enabled", requires.property());
        assertEquals("true", requires.value());
    }

    @Test
    void keyProviderMethodExists() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod(
                "encryptionKeyProvider", OjsConfiguration.class);
        assertNotNull(method);
        assertEquals(EncryptionMiddleware.KeyProvider.class, method.getReturnType());
    }

    @Test
    void encryptionMiddlewareMethodExists() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod(
                "encryptionMiddleware",
                EncryptionMiddleware.EncryptionCodec.class,
                EncryptionMiddleware.KeyProvider.class);
        assertNotNull(method);
        assertEquals(Middleware.class, method.getReturnType());
    }

    @Test
    void encryptionMiddlewareHasNamedAnnotation() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod(
                "encryptionMiddleware",
                EncryptionMiddleware.EncryptionCodec.class,
                EncryptionMiddleware.KeyProvider.class);
        var named = method.getAnnotation(jakarta.inject.Named.class);
        assertNotNull(named);
        assertEquals("ojsEncryption", named.value());
    }

    @Test
    void decryptionMiddlewareMethodExists() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod(
                "decryptionMiddleware",
                EncryptionMiddleware.EncryptionCodec.class,
                EncryptionMiddleware.KeyProvider.class);
        assertNotNull(method);
        assertEquals(Middleware.class, method.getReturnType());
    }

    @Test
    void decryptionMiddlewareHasNamedAnnotation() throws NoSuchMethodException {
        Method method = OjsFactory.class.getDeclaredMethod(
                "decryptionMiddleware",
                EncryptionMiddleware.EncryptionCodec.class,
                EncryptionMiddleware.KeyProvider.class);
        var named = method.getAnnotation(jakarta.inject.Named.class);
        assertNotNull(named);
        assertEquals("ojsDecryption", named.value());
    }

    @Test
    void keyProviderCreatedFromConfig() {
        var factory = new OjsFactory();
        var config = new OjsConfiguration();
        // 32 bytes for AES-256
        byte[] key = new byte[32];
        for (int i = 0; i < 32; i++) key[i] = (byte) i;
        config.setEncryptionKey(Base64.getEncoder().encodeToString(key));
        config.setEncryptionKeyId("test-key");

        var provider = factory.encryptionKeyProvider(config);
        assertNotNull(provider);
        assertInstanceOf(EncryptionMiddleware.StaticKeyProvider.class, provider);
    }

    @Test
    void encryptionCodecCreated() {
        var factory = new OjsFactory();
        var codec = factory.encryptionCodec();
        assertNotNull(codec);
        assertInstanceOf(EncryptionMiddleware.EncryptionCodec.class, codec);
    }
}
