package com.example;

import org.openjobspec.ojs.OJSClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final OJSClient client;

    public JobController(OJSClient client) {
        this.client = client;
    }

    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> enqueueEmail(@RequestBody Map<String, Object> body) {
        var job = client.enqueue("email.send", body);
        return ResponseEntity.ok(Map.of(
                "jobId", job.id(),
                "state", job.state()
        ));
    }
}
