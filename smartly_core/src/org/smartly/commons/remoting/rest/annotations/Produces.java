package org.smartly.commons.remoting.rest.annotations;

import java.lang.annotation.*;

@Inherited
@Target(value={ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Produces {
    String value();
}
