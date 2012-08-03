package org.smartly.packages.http.impl.handlers.rest.impl.annotations.meta;

import java.lang.annotation.*;

/**
 * Associates the name of a HTTP method with an annotation.
 * A Java method annotated with a runtime annotation that is itself annotated with
 * this annotation will be used to handle HTTP requests of the indicated HTTP method.
 * It is an error for a method to be annotated with more than one annotation that is
 * annotated with HttpMethod.
 */
@Target(value= ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {
    String value();
}
