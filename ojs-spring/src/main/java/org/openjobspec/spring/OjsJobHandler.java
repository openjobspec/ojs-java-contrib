package org.openjobspec.spring;

/**
 * Interface for Spring-managed OJS job handlers.
 *
 * <p>Implement this interface and annotate the class with {@link OjsJob}
 * for automatic registration:
 *
 * <pre>{@code
 * @OjsJob(type = "email.send", queue = "emails")
 * @Component
 * public class SendEmailJob implements OjsJobHandler {
 *     @Override
 *     public Object execute(OjsJobContext ctx) {
 *         String to = (String) ctx.job().argsMap().get("to");
 *         // send email...
 *         return Map.of("sent", true);
 *     }
 * }
 * }</pre>
 */
public interface OjsJobHandler {

    /**
     * Execute the job.
     *
     * @param ctx the job execution context
     * @return the job result (may be null)
     * @throws Exception if the handler fails
     */
    Object execute(OjsJobContext ctx) throws Exception;
}
