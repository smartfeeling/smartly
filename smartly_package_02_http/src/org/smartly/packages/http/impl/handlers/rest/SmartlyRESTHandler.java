package org.smartly.packages.http.impl.handlers.rest;


import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.http.impl.handlers.rest.impl.RESTRegistry;
import org.smartly.packages.http.impl.handlers.rest.impl.wrapper.MethodWrapper;
import org.smartly.packages.http.impl.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


/**
 * Standard REST handler.
 */
public class SmartlyRESTHandler extends ContextHandler {

    private static final String DEFAULT_ENDPOINT = "/rest";



    public SmartlyRESTHandler() {
        super.setContextPath(DEFAULT_ENDPOINT);
    }

    @Override
    public void setContextPath(final String contextPath) {
        super.setContextPath(contextPath);
    }

    @Override
    public void doHandle(final String target,
                         final Request baseRequest,
                         final HttpServletRequest request,
                         final HttpServletResponse response) throws IOException, ServletException {
        baseRequest.setHandled(true);
        this.handleInternal(target, baseRequest, request, response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getSmartlyLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void handleInternal(final String target,
                                final Request baseRequest,
                                final HttpServletRequest request,
                                final HttpServletResponse response) throws IOException, ServletException {
        final String method = request.getMethod();
        final String s1 = request.getRequestURI();
        final String path = request.getPathInfo();
        final String endPoint = request.getContextPath();


        if (StringUtils.hasText(path)) {
            final JSONObject formParams = ServletUtils.getParameters(request);
            final MethodWrapper mw = RESTRegistry.getMethod(method, path);
            if (null != mw) {
                final byte[] bytes = mw.execute(path, formParams);
                ServletUtils.writeResponse(response, DateUtils.now().getTime(), mw.getTypeOutput(), bytes);
            } else {
                response.sendError(HttpStatus.FORBIDDEN_403);
            }
        }
    }


}
