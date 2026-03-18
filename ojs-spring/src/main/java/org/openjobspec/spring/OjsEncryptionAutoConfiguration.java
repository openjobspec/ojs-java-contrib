package org.openjobspec.spring;

import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSWorker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

/**
 * Auto-configures OJS encryption middleware when {@code ojs.encryption.enabled=true}
 * and {@code org.openjobspec.ojs.EncryptionMiddleware} is available on the classpath.
 *
 * <p>Registers AES-256-GCM encryption on the client (encrypt before enqueue)
 * and decryption on the worker (decrypt before handler execution).
 *
 * <pre>{@code
 * ojs:
 *   encryption:
 *     enabled: true
 *     key: "base64-encoded-32-byte-AES-key"
 *     key-id: "key-2024"
 * }</pre>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.openjobspec.ojs.EncryptionMiddleware")
@ConditionalOnProperty(prefix = "ojs.encryption", name = "enabled", havingValue = "true")
public class OjsEncryptionAutoConfiguration {

    private final OjsProperties properties;
    private final OJSWorker worker;

    public OjsEncryptionAutoConfiguration(OjsProperties properties,
                                           OJSWorker worker) {
        this.properties = properties;
        this.worker = worker;
    }

    /**
     * Configures encryption middleware using reflection to avoid a compile-time
     * dependency on EncryptionMiddleware (which may not be present in all SDK versions).
     */
    @PostConstruct
    void configureEncryption() {
        var encProps = properties.getEncryption();
        Objects.requireNonNull(encProps.getKey(),
                "ojs.encryption.key must be set when encryption is enabled");
        Objects.requireNonNull(encProps.getKeyId(),
                "ojs.encryption.key-id must be set when encryption is enabled");

        try {
            byte[] keyBytes = Base64.getDecoder().decode(encProps.getKey());

            Class<?> encClass = Class.forName("org.openjobspec.ojs.EncryptionMiddleware");
            Class<?> codecClass = Class.forName(
                    "org.openjobspec.ojs.EncryptionMiddleware$EncryptionCodec");
            Class<?> keyProviderClass = Class.forName(
                    "org.openjobspec.ojs.EncryptionMiddleware$StaticKeyProvider");
            Class<?> keyProviderInterface = Class.forName(
                    "org.openjobspec.ojs.EncryptionMiddleware$KeyProvider");

            Object codec = codecClass.getDeclaredConstructor().newInstance();
            Object keyProvider = keyProviderClass
                    .getDeclaredConstructor(Map.class, String.class)
                    .newInstance(Map.of(encProps.getKeyId(), keyBytes), encProps.getKeyId());

            Method encryptMethod = encClass.getMethod(
                    "encryptionMiddleware", codecClass, keyProviderInterface);
            Method decryptMethod = encClass.getMethod(
                    "decryptionMiddleware", codecClass, keyProviderInterface);

            Middleware encryptMw = (Middleware) encryptMethod.invoke(null, codec, keyProvider);
            Middleware decryptMw = (Middleware) decryptMethod.invoke(null, codec, keyProvider);

            // Worker gets both encrypt and decrypt middleware
            worker.use("ojs-encrypt", encryptMw);
            worker.use("ojs-decrypt", decryptMw);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "EncryptionMiddleware not found on classpath.", e);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to configure OJS encryption middleware", e);
        }
    }
}
