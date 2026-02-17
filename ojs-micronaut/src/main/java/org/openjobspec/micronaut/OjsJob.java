package org.openjobspec.micronaut;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an OJS job handler for the specified job type.
 *
 * <pre>{@code
 * @Singleton
 * public class EmailJob {
 *     @OjsJob("email.send")
 *     public Object handle(JobContext ctx) {
 *         // process job...
 *         return Map.of("sent", true);
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OjsJob {

    /** The OJS job type this handler processes (e.g. "email.send"). */
    String value();
}
