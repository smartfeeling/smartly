package org.smartly.commons.remoting.rest.annotations;

import org.smartly.commons.remoting.rest.annotations.meta.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod(value = "GET")
public @interface GET {

}
