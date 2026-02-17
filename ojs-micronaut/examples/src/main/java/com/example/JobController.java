package com.example;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import org.openjobspec.ojs.OJSClient;

import java.util.Map;

@Controller("/jobs")
public class JobController {

    @Inject
    OJSClient client;

    @Post("/email")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> enqueueEmail(@Body Map<String, Object> body) {
        var job = client.enqueue("email.send", body);
        return Map.of(
                "jobId", job.id(),
                "state", job.state()
        );
    }
}
