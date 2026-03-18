package org.openjobspec.micronaut;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.EncryptionMiddleware;
import org.openjobspec.ojs.Middleware;
import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;

import java.util.Base64;
import java.util.Map;

/**
 * Micronaut factory producing OJS client, worker, and encryption beans.
 */
@Factory
public class OjsFactory {

    @Singleton
    public OJSClient ojsClient(OjsConfiguration config) {
        return OJSClient.builder()
                .url(config.getUrl())
                .build();
    }

    @Singleton
    public OJSWorker ojsWorker(OjsConfiguration config) {
        return OJSWorker.builder()
                .url(config.getUrl())
                .queues(config.getQueues())
                .concurrency(config.getConcurrency())
                .build();
    }

    /**
     * Creates an {@link EncryptionMiddleware.EncryptionCodec} bean when encryption is enabled.
     */
    @Singleton
    @Requires(property = "ojs.encryption-enabled", value = "true")
    public EncryptionMiddleware.EncryptionCodec encryptionCodec() {
        return new EncryptionMiddleware.EncryptionCodec();
    }

    /**
     * Creates a {@link EncryptionMiddleware.KeyProvider} from configuration when encryption is enabled.
     */
    @Singleton
    @Requires(property = "ojs.encryption-enabled", value = "true")
    public EncryptionMiddleware.KeyProvider encryptionKeyProvider(OjsConfiguration config) {
        byte[] keyBytes = Base64.getDecoder().decode(config.getEncryptionKey());
        return new EncryptionMiddleware.StaticKeyProvider(
                Map.of(config.getEncryptionKeyId(), keyBytes),
                config.getEncryptionKeyId()
        );
    }

    /**
     * Encryption middleware for the client side (encrypts job args before enqueue).
     */
    @Singleton
    @Named("ojsEncryption")
    @Requires(property = "ojs.encryption-enabled", value = "true")
    public Middleware encryptionMiddleware(
            EncryptionMiddleware.EncryptionCodec codec,
            EncryptionMiddleware.KeyProvider keys) {
        return EncryptionMiddleware.encryptionMiddleware(codec, keys);
    }

    /**
     * Decryption middleware for the worker side (decrypts job args before handler).
     */
    @Singleton
    @Named("ojsDecryption")
    @Requires(property = "ojs.encryption-enabled", value = "true")
    public Middleware decryptionMiddleware(
            EncryptionMiddleware.EncryptionCodec codec,
            EncryptionMiddleware.KeyProvider keys) {
        return EncryptionMiddleware.decryptionMiddleware(codec, keys);
    }
}
