package org.smartly.packages.http.impl.handlers.rest.impl.annotations;

import java.lang.annotation.*;

@Inherited
@Target(value={ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
    String value();
}
