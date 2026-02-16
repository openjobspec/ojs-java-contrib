package org.openjobspec.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an OJS job handler for the specified job type.
 *
 * <pre>{@code
 * @Component
 * public class EmailJob {
 *     @OjsJob("email.send")
 *     public Object handle(JobContext ctx) {
 *         // process job...
 *         return Map.of("sent", true);
 *     }
 * }
 * }</pre>
 *
 * <p>The annotated method must accept a single {@link org.openjobspec.ojs.JobContext}
 * parameter and may return an {@link Object} result (or void).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OjsJob {

    /** The OJS job type this handler processes (e.g. "email.send"). */
    String value();
}
