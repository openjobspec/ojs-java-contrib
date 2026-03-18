package org.openjobspec.quarkus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.openjobspec.ojs.EncryptionMiddleware;
import org.openjobspec.ojs.EncryptionMiddleware.EncryptionCodec;
import org.openjobspec.ojs.EncryptionMiddleware.KeyProvider;
import org.openjobspec.ojs.EncryptionMiddleware.StaticKeyProvider;
import org.openjobspec.ojs.Middleware;

import java.util.Base64;
import java.util.Map;
import java.util.logging.Logger;

/**
 * CDI producer for OJS encryption and decryption middleware.
 *
 * <p>When {@code ojs.encryption.enabled=true} and a Base64-encoded key is
 * provided via {@code ojs.encryption.key}, this producer creates an
 * AES-256-GCM encryption/decryption middleware pair. The middleware is
 * automatically installed on the worker by {@link OjsWorkerLifecycle}.</p>
 *
 * <p>Inject by qualifier name:</p>
 * <pre>{@code
 * @Inject @Named("ojsEncryption") Middleware encryption;
 * @Inject @Named("ojsDecryption") Middleware decryption;
 * }</pre>
 */
@ApplicationScoped
public class OjsEncryptionProducer {

    private static final Logger LOG = Logger.getLogger(OjsEncryptionProducer.class.getName());

    /**
     * Produces the encryption middleware (for the client / enqueue side).
     * Returns a pass-through middleware when encryption is disabled.
     */
    @Produces
    @Singleton
    @Named("ojsEncryption")
    public Middleware encryptionMiddleware(OjsConfig config) {
        if (!config.encryption().enabled() || config.encryption().key().isEmpty()) {
            LOG.fine("OJS encryption disabled — producing pass-through middleware");
            return (ctx, next) -> next.handle(ctx);
        }

        var keyProvider = buildKeyProvider(config);
        var codec = new EncryptionCodec();
        LOG.info("OJS encryption middleware enabled (keyId=" + config.encryption().keyId() + ")");
        return EncryptionMiddleware.encryptionMiddleware(codec, keyProvider);
    }

    /**
     * Produces the decryption middleware (for the worker / handler side).
     * Returns a pass-through middleware when encryption is disabled.
     */
    @Produces
    @Singleton
    @Named("ojsDecryption")
    public Middleware decryptionMiddleware(OjsConfig config) {
        if (!config.encryption().enabled() || config.encryption().key().isEmpty()) {
            LOG.fine("OJS decryption disabled — producing pass-through middleware");
            return (ctx, next) -> next.handle(ctx);
        }

        var keyProvider = buildKeyProvider(config);
        var codec = new EncryptionCodec();
        LOG.info("OJS decryption middleware enabled (keyId=" + config.encryption().keyId() + ")");
        return EncryptionMiddleware.decryptionMiddleware(codec, keyProvider);
    }

    private KeyProvider buildKeyProvider(OjsConfig config) {
        var keyId = config.encryption().keyId();
        var keyBytes = Base64.getDecoder().decode(config.encryption().key().orElseThrow());
        return new StaticKeyProvider(Map.of(keyId, keyBytes), keyId);
    }
}
