package org.openjobspec.spring;

import org.openjobspec.ojs.OJSClient;
import org.openjobspec.ojs.OJSWorker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for OJS client and worker beans.
 * Activated when {@code ojs.enabled} is {@code true} (the default).
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
                .queues(properties.getQueues())
                .concurrency(properties.getConcurrency())
                .build();
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
        public OjsHealthIndicator ojsHealthIndicator(OJSClient client) {
            return new OjsHealthIndicator(client);
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
