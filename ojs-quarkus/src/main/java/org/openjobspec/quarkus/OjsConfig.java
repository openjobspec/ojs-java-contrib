package org.openjobspec.quarkus;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.List;
import java.util.Optional;

/**
 * Quarkus configuration mapping for OJS properties.
 */
@ConfigMapping(prefix = "ojs")
public interface OjsConfig {

    /** OJS backend URL. */
    @WithDefault("http://localhost:8080")
    String url();

    /** Queues the worker should poll. */
    @WithDefault("default")
    List<String> queues();

    /** Worker concurrency. */
    @WithDefault("10")
    int concurrency();
}
