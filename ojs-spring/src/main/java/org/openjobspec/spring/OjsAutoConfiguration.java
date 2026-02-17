package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for OJS client, worker, template, and supporting beans.
 * Activated when {@code ojs.enabled} is {@code true} (the default).
 *
 * <p>This configuration:
 * <ul>
 *   <li>Creates an {@link OJSClient} from {@code ojs.url}</li>
 *   <li>Creates an {@link OJSWorker} with auto-registration of {@link OjsJob @OjsJob} handlers</li>
 *   <li>Provides an {@link OjsTemplate} for Spring-style job operations</li>
 *   <li>Configures {@link OjsHealthIndicator} when Spring Actuator is on the classpath</li>
 *   <li>Configures {@link OjsMicrometerMetrics} when Micrometer is on the classpath</li>
 *   <li>Configures {@link OjsTransactionalEnqueue} when Spring TX is on the classpath</li>
 * </ul>
 */
@AutoConfiguration
@EnableConfigurationProperties(OjsProperties.class)
@ConditionalOnProperty(prefix = "ojs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OjsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OJSClient ojsClient(OjsProperties properties) {
        return OJSClient.builder()
                .url(properties.getUrl())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OJSWorker ojsWorker(OjsProperties properties) {
        return OJSWorker.builder()
                .url(properties.getUrl())
                .queues(properties.resolvedQueues())
                .concurrency(properties.resolvedConcurrency())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OjsTemplate ojsTemplate(OJSClient client, OjsProperties properties) {
        return new OjsTemplate(client, properties.getDefaultQueue());
    }

    @Bean
    public OjsJobRegistrar ojsJobRegistrar(OJSWorker worker) {
        return new OjsJobRegistrar(worker);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
    static class OjsHealthAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OjsHealthIndicator ojsHealthIndicator(OJSClient client,
                                                      org.springframework.beans.factory.ObjectProvider<OJSWorker> workerProvider) {
            return new OjsHealthIndicator(client, workerProvider.getIfAvailable());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "io.micrometer.core.instrument.MeterRegistry")
    @ConditionalOnBean(io.micrometer.core.instrument.MeterRegistry.class)
    static class OjsMicrometerAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OjsMicrometerMetrics ojsMicrometerMetrics(
                io.micrometer.core.instrument.MeterRegistry registry) {
            return new OjsMicrometerMetrics(registry);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.transaction.support.TransactionSynchronizationManager")
    static class OjsTransactionalAutoConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public OjsTransactionalEnqueue ojsTransactionalEnqueue(OJSClient client) {
            return new OjsTransactionalEnqueue(client);
        }
    }
}
