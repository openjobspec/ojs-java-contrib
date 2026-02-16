package com.example;

import org.openjobspec.ojs.JobContext;
import org.openjobspec.spring.OjsJob;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailJob {

    @OjsJob("email.send")
    public Object handle(JobContext ctx) {
        var args = ctx.job().argsMap();
        var to = (String) args.get("to");
        var subject = (String) args.get("subject");

        System.out.printf("Sending email to=%s subject=%s%n", to, subject);

        return Map.of("sent", true, "to", to);
    }
}
