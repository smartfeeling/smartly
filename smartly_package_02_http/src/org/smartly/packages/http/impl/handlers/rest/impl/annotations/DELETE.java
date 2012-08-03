package org.smartly.packages.http.impl.handlers.rest.impl.annotations;

import org.smartly.packages.http.impl.handlers.rest.impl.annotations.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod(value = "DELETE")
public @interface DELETE {

}
