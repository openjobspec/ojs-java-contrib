package org.openjobspec.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSWorker;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OjsEncryptionAutoConfigurationTest {

    @Mock
    OJSWorker worker;

    @Test
    void throwsWhenKeyMissing() {
        var props = new OjsProperties();
        props.getEncryption().setEnabled(true);
        props.getEncryption().setKeyId("test-key");

        var config = new OjsEncryptionAutoConfiguration(props, worker);
        assertThrows(NullPointerException.class, config::configureEncryption);
    }

    @Test
    void throwsWhenKeyIdMissing() {
        var props = new OjsProperties();
        props.getEncryption().setEnabled(true);
        byte[] key = new byte[32];
        props.getEncryption().setKey(Base64.getEncoder().encodeToString(key));

        var config = new OjsEncryptionAutoConfiguration(props, worker);
        assertThrows(NullPointerException.class, config::configureEncryption);
    }

    @Test
    void registersEncryptAndDecryptMiddlewareWhenClassAvailable() {
        var props = new OjsProperties();
        props.getEncryption().setEnabled(true);
        byte[] key = new byte[32];
        props.getEncryption().setKey(Base64.getEncoder().encodeToString(key));
        props.getEncryption().setKeyId("test-key-2024");

        var config = new OjsEncryptionAutoConfiguration(props, worker);
        // EncryptionMiddleware is present on the classpath (SDK >= 0.3.0), so the
        // reflective wiring must succeed and install both middlewares on the worker.
        assertDoesNotThrow(config::configureEncryption);

        verify(worker).use(eq("ojs-encrypt"), any(Middleware.class));
        verify(worker).use(eq("ojs-decrypt"), any(Middleware.class));
    }
}
