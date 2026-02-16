package com.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.openjobspec.ojs.OJSClient;

import java.util.Map;

@Path("/jobs")
public class JobResource {

    @Inject
    OJSClient client;

    @POST
    @Path("/email")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> enqueueEmail(Map<String, Object> body) {
        var job = client.enqueue("email.send", body);
        return Map.of(
                "jobId", job.id(),
                "state", job.state()
        );
    }
}
