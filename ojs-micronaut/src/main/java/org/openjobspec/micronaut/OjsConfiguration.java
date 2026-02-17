package org.openjobspec.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;

import java.util.List;

/**
 * Micronaut configuration properties for OJS, bound from {@code ojs.*}.
 */
@ConfigurationProperties("ojs")
public class OjsConfiguration {

    /** OJS backend URL. */
    private String url = "http://localhost:8080";

    /** Queues the worker should poll. */
    private List<String> queues = List.of("default");

    /** Worker concurrency. */
    private int concurrency = 10;

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
}
