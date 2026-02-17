package org.openjobspec.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or class as an OJS job handler for the specified job type.
 *
 * <p><b>Method-level usage:</b></p>
 * <pre>{@code
 * @Component
 * public class EmailJob {
 *     @OjsJob(type = "email.send", queue = "emails")
 *     public Object handle(JobContext ctx) {
 *         // process job...
 *         return Map.of("sent", true);
 *     }
 * }
 * }</pre>
 *
 * <p><b>Class-level usage (requires implementing {@link OjsJobHandler}):</b></p>
 * <pre>{@code
 * @OjsJob(type = "email.send", queue = "emails")
 * @Component
 * public class SendEmailJob implements OjsJobHandler {
 *     @Override
 *     public Object execute(OjsJobContext ctx) {
 *         return Map.of("sent", true);
 *     }
 * }
 * }</pre>
 *
 * <p>For method-level usage, the annotated method must accept a single
 * {@link org.openjobspec.ojs.JobContext} parameter and may return an {@link Object}
 * result (or void).
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OjsJob {

    /**
     * The OJS job type this handler processes (e.g. "email.send").
     * Alias for {@link #type()}.
     */
    String value() default "";

    /** The OJS job type this handler processes (e.g. "email.send"). */
    String type() default "";

    /** The queue to use for this job type. Defaults to the configured default queue. */
    String queue() default "";
}
