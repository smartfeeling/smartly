package org.smartly.packages.http.impl;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 *
 *
 */
public interface IHttpHandler {

    boolean handle (final ServletRequest request, final ServletResponse response) throws Exception;

}
