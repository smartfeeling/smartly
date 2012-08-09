package org.smartly.packages.http.impl.handlers.rest;


import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.ByteUtils;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.http.impl.handlers.rest.impl.RESTRegistry;
import org.smartly.packages.http.impl.handlers.rest.impl.wrapper.MethodWrapper;
import org.smartly.packages.http.impl.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Standard REST handler.
 */
public class SmartlyRESTHandler extends ContextHandler {

    private static final String DEFAULT_ENDPOINT = "/rest";

    private static final String REQ_PARAM_SEP = "&";


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
        //-- verify if "smartly_remoting" is loaded--//
        if (Smartly.hasPackage("smartly_remoting")) {
            baseRequest.setHandled(true);
            this.handleInternal(target, baseRequest, request, response);
        } else {
            baseRequest.setHandled(false);
        }
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
            final Map<String, Object> formParams = this.getParameters(request);
            final MethodWrapper mw = RESTRegistry.getMethod(method, path);
            if (null != mw) {
                final byte[] bytes = mw.execute(path, formParams);
                ServletUtils.writeResponse(response, DateUtils.now().getTime(), mw.getTypeOutput(), bytes);
            } else {
                response.sendError(HttpStatus.FORBIDDEN_403);
            }
        }
    }

    private Map<String, Object> getParameters(final HttpServletRequest request) {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        //-- GET METHOD --//
        try {
            final Map<String, String[]> map = request.getParameterMap();
            if (map.size() > 0) {
                final Set<String> keys = map.keySet();
                for (final String key : keys) {
                    if (null != key) {
                        final String[] value = map.get(key);
                        if (value.length > 0) {
                            this.addParam(result, key, value[0]);
                        } else {
                            this.addParam(result, key, "");
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        //-- POST METHOD --//
        try {
            final InputStream is = request.getInputStream();
            final byte[] bytes = ByteUtils.getBytes(is);
            if (null != bytes && bytes.length>0) {
                final String data = new String(bytes, Smartly.getCharset());
                if (StringUtils.hasLength(data)) {
                    final String[] queryTokens = StringUtils.split(data, REQ_PARAM_SEP);
                    for (final String qt : queryTokens) {
                        final String[] keyValue = StringUtils.split(qt, "=");
                        if (keyValue.length == 2) {
                            result.put(keyValue[0], this.decode(keyValue[1]));
                        } else {
                            result.put(keyValue[0], "");
                        }
                    }
                }
            }
            is.close();
        } catch (Throwable ignored) {
        }


        return result;
    }

    private void addParam(final Map<String, Object> params, final String key, final String value){
        if(key.indexOf("[")>0 && key.endsWith("]")){
            final String k1 = key.substring(0, key.indexOf("["));
            final String k2 = key.substring(key.indexOf("[")+1, key.lastIndexOf("]"));
            if(!params.containsKey(k1)){
                params.put(k1, new JSONObject());
            }
            JsonWrapper.put((JSONObject) params.get(k1), k2, value);
        } else {
            params.put(key, value);
        }
    }

    private String decode(final String value) {
        try {
            return URLDecoder.decode(value, Smartly.getCharset());
        } catch (Exception ex) {
            return value;
        }
    }


}
