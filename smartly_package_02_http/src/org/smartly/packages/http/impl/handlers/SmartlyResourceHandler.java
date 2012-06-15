package org.smartly.packages.http.impl.handlers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class SmartlyResourceHandler extends org.eclipse.jetty.server.handler.ResourceHandler {

    @Override
    public void handle(final String target,
                       final Request baseRequest,
                       final HttpServletRequest request,
                       final HttpServletResponse response) throws IOException, ServletException {

        super.handle(target, baseRequest, request, response);
    }

    @Override
    protected void doResponseHeaders(final HttpServletResponse response,
                                     final Resource resource,
                                     final String mimeType) {
        super.doResponseHeaders(response, resource, mimeType);
    }
}
