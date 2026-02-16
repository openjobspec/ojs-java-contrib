package org.openjobspec.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Configuration properties for OJS, bound from {@code ojs.*} in application.yml/properties.
 */
@ConfigurationProperties(prefix = "ojs")
public class OjsProperties {

    /** OJS backend URL. */
    private String url = "http://localhost:8080";

    /** Queues the worker should poll. */
    private List<String> queues = List.of("default");

    /** Worker concurrency (number of virtual threads processing jobs). */
    private int concurrency = 10;

    /** Enable or disable OJS auto-configuration. */
    private boolean enabled = true;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getQueues() {
        return queues;
    }

    public void setQueues(List<String> queues) {
        this.queues = queues;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
