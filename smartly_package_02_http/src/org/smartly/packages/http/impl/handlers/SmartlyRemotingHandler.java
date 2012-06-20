package org.smartly.packages.http.impl.handlers;


import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.http.impl.serialization.json.JsonSerializer;
import org.smartly.packages.http.impl.util.BinaryData;
import org.smartly.packages.http.impl.util.ServletUtils;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * This Handler depends from "smartly_remoting" package.
 * If "smartly_remoting" is not included you cannot use this remoting handler.
 * <p/>
 * Disable this handler or remove declaration from cong file if you don't intend to use it.
 */
public class SmartlyRemotingHandler extends ContextHandler {

    private static final String ENDPOINT = "/rest";

    private static final String REQ_PARAM_SEP = "&";

    private static final String MIMETYPE_JSON = "application/json;charset=UTF-8";
    private static final String MIMETYPE_PNG = "image/png";
    private static final String MIMETYPE_TEXT = "text/plain;charset=UTF-8";
    private static final String MIMETYPE_ZIP = "application/zip";

    private static final String FORMAT_JSON = "json";
    private static final String FORMAT_XML = "xml";
    private static final String FORMAT_BIN = "bin";


    public SmartlyRemotingHandler() {
        super.setContextPath(ENDPOINT);
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
        if (method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("POST")) {
            final String s1 = request.getRequestURI();
            final String path = request.getPathInfo();
            final String endPoint = request.getContextPath();
            final String[] tokens = StringUtils.split(path, "/");
            final String responseFormat;
            final String serviceName;
            final String methodName;
            if (tokens.length == 2) {
                responseFormat = FORMAT_JSON;
                serviceName = tokens[0];
                methodName = tokens[1];
            } else if (tokens.length == 3) {
                responseFormat = tokens[0];
                serviceName = tokens[1];
                methodName = tokens[2];
            } else {
                // invalid request
                responseFormat = FORMAT_JSON;
                serviceName = null;
                methodName = null;
            }

            // ready to invoke remote service
            try {
                if (StringUtils.hasText(methodName) && StringUtils.hasText(serviceName)) {
                    final Map<String, String> params = this.getParameters(method, request);
                    final Class invokerClass = ClassLoaderUtils.forName("org.smartly.packages.remoting.impl.RemoteInvoker");
                    final Object invoker = ClassLoaderUtils.newInstance(invokerClass);
                    if (null != invoker) {
                        final Method invokeMethod = invokerClass.getMethod("call",
                                String.class, String.class, String.class, Map.class);
                        if (null != invokeMethod) {
                            final Object invokeResponse = invokeMethod.invoke(invoker,
                                    endPoint,
                                    serviceName,
                                    methodName,
                                    params);
                            this.writeResponse(response, responseFormat, invokeResponse);
                        }
                    } else {
                        throw new Exception("INVOKER NOT LOADED. REMOTING IS NOT AVAILABLE.");
                    }
                } else {
                    throw new Exception("BAD REQUEST FORMAT.");
                }
            } catch (Throwable t) {
                // service not found or execution error
                this.writeResponse(response, FORMAT_JSON, t);
            }
        }
    }

    private Map<String, String> getParameters(final String method, final HttpServletRequest request) {
        final Map<String, String> result = new LinkedHashMap<String, String>();
        if (method.equalsIgnoreCase("GET")) {
            //-- GET METHOD --//
            final Map<String, String[]> map = request.getParameterMap();
            if (map.size() > 0) {
                final Set<String> keys = map.keySet();
                for (final String key : keys) {
                    final String[] value = map.get(key);
                    if (null != key && key.length() > 0) {
                        result.put(key, value[0]);
                    } else {
                        result.put(key, "");
                    }
                }
            }
        } else {
            //-- POST METHOD --//
            try {
                final InputStream is = request.getInputStream();
                final byte[] bytes = ByteUtils.getBytes(is);
                if (null != bytes) {
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
        }

        return result;
    }

    private String decode(final String value) {
        try {
            return URLDecoder.decode(value, Smartly.getCharset());
        } catch (Exception ex) {
            return value;
        }
    }

    private void writeResponse(final HttpServletResponse response, final String format, final Object data) {
        try {
            if (FORMAT_JSON.equalsIgnoreCase(format)) {
                final String serialized = JsonSerializer.serialize(data);
                final byte[] bytes = serialized.getBytes(Smartly.getCharset());
                ServletUtils.doResponseHeaders(response, null, bytes.length, MIMETYPE_JSON);
                ServletUtils.writeResponse(response, DateUtils.now().getTime(), bytes);
            } else if (FORMAT_XML.equalsIgnoreCase(format)) {
                // not supported yet
            } else if (FORMAT_BIN.equalsIgnoreCase(format)) {
                if (data instanceof BinaryData) {
                    final BinaryData bin_data = (BinaryData) data;
                    final byte[] bytes = bin_data.getBytes();
                    ServletUtils.doResponseHeaders(response, null, bytes.length, bin_data.getMimetype());
                    ServletUtils.writeResponse(response, DateUtils.now().getTime(), bytes);
                } else if (ByteUtils.isByteArray(data)) {
                    final byte[] bytes = (byte[]) data;
                    ServletUtils.doResponseHeaders(response, null, bytes.length, MIMETYPE_PNG);
                    ServletUtils.writeResponse(response, DateUtils.now().getTime(), bytes);
                } else if (data instanceof String) {
                    final byte[] bytes = ((String) data).getBytes(Smartly.getCharset());
                    ServletUtils.doResponseHeaders(response, null, bytes.length, MIMETYPE_TEXT);
                    ServletUtils.writeResponse(response, DateUtils.now().getTime(), bytes);
                } else if (data instanceof InputStream) {
                    final byte[] bytes = ByteUtils.getBytes((InputStream) data);
                    ServletUtils.doResponseHeaders(response, null, bytes.length, MIMETYPE_ZIP);
                    ServletUtils.writeResponse(response, DateUtils.now().getTime(), bytes);
                } else {
                    // data not supported
                }
            } else {
                this.writeResponse(response, FORMAT_JSON, new Exception(
                        FormatUtils.format("Unsupported format: '{0}'", format)));
            }
        } catch (Throwable t) {
            this.getSmartlyLogger().log(Level.SEVERE,
                    FormatUtils.format("ERROR WRITING RESPONSE: {0}", t), t);
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      c l a s s  -  p r i v a t e
    // ------------------------------------------------------------------------

    private class Listener implements AsyncListener {

        private final Logger _logger;
        private final String _uri;

        private Listener(final Logger logger, final String uri) {
            _logger = logger;
            _uri = uri;
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            ;
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            _logger.log(Level.WARNING, FormatUtils.format("Request time out for '{0}'", _uri));
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {

        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {

        }
    }
}
