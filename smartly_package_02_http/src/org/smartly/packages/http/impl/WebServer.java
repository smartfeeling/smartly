package org.smartly.packages.http.impl;


import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.http.impl.handlers.SmartlyShutdownHandler;
import org.smartly.packages.http.impl.util.vtool.AppTool;
import org.smartly.packages.velocity.impl.VLCManager;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Web Server Wrapper
 * <p/>
 * More about Jetty here:
 * http://git.eclipse.org/c/jetty/org.eclipse.jetty.project.git/tree/examples/embedded/src/main/java/org/eclipse/jetty/embedded/
 */
public class WebServer
        extends AbstractHttpServer {


    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    private static final String KEYSTORE = "/keystore";

    public static final String PARAM_REQUEST_BUFFER = "request_buffer";
    public static final String PARAM_RESPONSE_BUFFER = "response_buffer";
    public static final String PARAM_SECURE_PORT = "secure_port";
    public static final String PARAM_SECURE_SCHEME = "secure_scheme";
    public static final String PARAM_CONNECTORS = "connectors";
    public static final String PARAM_ENABLED = "enabled";
    public static final String PARAM_PORT = "port";
    public static final String PARAM_MAX_IDLE = "max_idle";
    public static final String PARAM_KEY_PASSWORD = "key_password";
    public static final String PARAM_KEY_MANAGER_PASSWORD = "key_manager_password";
    public static final String PARAM_HANDLERS = "handlers";
    public static final String PARAM_CHAIN = "chain";
    public static final String PARAM_ENDPOINTS = "endpoints";
    public static final String PARAM_ENDPOINT = "endpoint";
    public static final String PARAM_SERVLETS = "servlets";
    public static final String PARAM_DATA = "data";
    public static final String PARAM_MULTIPART = "multipart";
    public static final String PARAM_MULTIPART_LOCATION = "multipart_location";
    public static final String PARAM_MULTIPART_MAX_FILE = "multipart_max_file";
    public static final String PARAM_MULTIPART_MAX_REQUEST = "multipart_max_request";
    public static final String PARAM_MULTIPART_FILE_THRESHOLD = "multipart_file_threshold";

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private Connector[] _connectors;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public WebServer(final String docRoot,
                     final JSONObject configuration) {
        super(docRoot, configuration);
    }

    @Override
    public void start(final boolean join) {
        try {
            final String jettyHome = super.getRoot(); // absolute path
            final String sslRoot = super.getSslRootPath();
            final JSONObject configuration = super.getConfiguration();


            //-- init connectors --//
            _connectors = initConnectors(super.getJetty(), sslRoot, configuration);

            super.getJetty().setConnectors(_connectors);

            //-- init handlers --//
            final Handler handler = initHandlers(this, configuration);
            if (null != handler) {
                super.getJetty().setHandler(handler);
            }

            // init velocity engine
            initVelocity(jettyHome);

            if (!Smartly.isTestUnitMode()) {
                //-- start jetty --//
                super.start(join);
            }
        } catch (Throwable t) {
            super.triggerOnError("Error starting server", t);
            // try stop server
            try {
                super.stop();
            } catch (Throwable t2) {
                super.triggerOnError("Error stopping server", t2);
            }
        }
    }

    @Override
    public String toString(){
        final StringBuilder result = new StringBuilder();
        result.append(super.getJetty().toString());
        return result.toString();
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static void initVelocity(final String absoluteDocRoot) {
        VLCManager.getInstance().getEngine().setFileResourceLoaderPath(absoluteDocRoot);

        //-- APPLICATION TOOL (DON'T override) --//
        VLCManager.getInstance().getToolbox().add(AppTool.NAME, AppTool.class, null, true);
    }

    private static Logger staticLogger() {
        return LoggingUtils.getLogger(WebServer.class);
    }

    private static String mkdirs(final String absolutePath) {
        try {
            FileUtils.mkdirs(absolutePath);
        } catch (Throwable t) {
            staticLogger().log(Level.SEVERE, null, t);
        }
        return absolutePath;
    }

    private static Connector[] initConnectors(final Server server,
                                              final String sslRoot,
                                              final JSONObject configuration) {
        final int request_buffer = JsonWrapper.getInt(configuration, PARAM_REQUEST_BUFFER, 8112);
        final int response_buffer = JsonWrapper.getInt(configuration, PARAM_RESPONSE_BUFFER, 32768);
        final int secure_port = JsonWrapper.getInt(configuration, PARAM_SECURE_PORT, 8443);
        final String secure_scheme = JsonWrapper.getString(configuration, PARAM_SECURE_SCHEME, "https");
        final JSONObject connectors = JsonWrapper.getJSON(configuration, PARAM_CONNECTORS);
        final Iterator<String> keys = connectors.keys();
        final List<Connector> result = new LinkedList<Connector>();

        // http configuration
        final HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme(secure_scheme);
        http_config.setSecurePort(secure_port);
        http_config.setOutputBufferSize(response_buffer);
        http_config.setRequestHeaderSize(request_buffer);

        while (keys.hasNext()) {
            final String key = keys.next();

            if ("http".equalsIgnoreCase(key)) {
                final JSONObject connector = JsonWrapper.getJSON(connectors, key);
                if (null != connector && JsonWrapper.getBoolean(connector, PARAM_ENABLED)) {

                    final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
                    http.setPort(JsonWrapper.getInt(connector, PARAM_PORT, 8080));
                    http.setIdleTimeout(JsonWrapper.getInt(connector, PARAM_MAX_IDLE, 30000));
                    result.add(http);
                }
            } else if ("ssl".equalsIgnoreCase(key)) {
                final JSONObject connector = JsonWrapper.getJSON(connectors, key);
                if (null != connector && JsonWrapper.getBoolean(connector, PARAM_ENABLED)) {

                    mkdirs(sslRoot);
                    final String keySorePath = PathUtils.join(sslRoot, KEYSTORE);

                    // SSL Context Factory for HTTPS and SPDY
                    SslContextFactory sslContextFactory = new SslContextFactory();
                    sslContextFactory.setKeyStorePath(keySorePath);
                    sslContextFactory.setKeyStorePassword(JsonWrapper.getString(connector, PARAM_KEY_PASSWORD));
                    sslContextFactory.setKeyManagerPassword(JsonWrapper.getString(connector, PARAM_KEY_MANAGER_PASSWORD));

                    // HTTPS Configuration
                    HttpConfiguration https_config = new HttpConfiguration(http_config);
                    https_config.addCustomizer(new SecureRequestCustomizer());

                    // HTTPS connector
                    ServerConnector https = new ServerConnector(server,
                            new SslConnectionFactory(sslContextFactory, "http/1.1"),
                            new HttpConnectionFactory(https_config));
                    https.setPort(JsonWrapper.getInt(connector, PARAM_PORT, 8443));
                    https.setIdleTimeout(JsonWrapper.getInt(connector, PARAM_MAX_IDLE, 30000));

                    result.add(https);
                }
            }
        }
        return result.toArray(new Connector[result.size()]);
    }

    /**
     * Returns main Handler. It's a HandlerList.
     * A Handler List is a Handler Collection that calls each handler in turn until
     * either an exception is thrown,
     * the response is committed or
     * the request.isHandled() returns true.
     * It can be used to combine handlers that conditionally handle a request.
     *
     * @param server
     * @param configuration
     * @return
     */
    private static Handler initHandlers(final WebServer server, final JSONObject configuration) {
        final String shutdownToken = JsonWrapper.getString(configuration, "shutdown_token", "smartly");

        final JSONObject handlers = JsonWrapper.getJSON(configuration, PARAM_HANDLERS);
        if (null != handlers && handlers.length() > 0) {

            //-- file handler --//
            final HandlerList main = initChainHandlers(server, JsonWrapper.getJSON(handlers, PARAM_CHAIN));

            //-- shutdown handler --//
            main.addHandler(new SmartlyShutdownHandler(server, shutdownToken));

            //-- add endpoints (servlets and rest) --//
            final ContextHandlerCollection endpoints = initContextHandlers(server, JsonWrapper.getJSON(handlers, PARAM_ENDPOINTS));
            if (null != endpoints) {
                main.addHandler(endpoints);
            }

            return main;
        }

        return null;
    }

    private static HandlerList initChainHandlers(final WebServer server, final JSONObject chain) {
        final HandlerList list = new HandlerList();
        if (null != chain && JsonWrapper.getBoolean(chain, PARAM_ENABLED)) {
            final List<JSONObject> data = JsonWrapper.toListOfJSONObject(JsonWrapper.getArray(chain, PARAM_DATA));
            if (!CollectionUtils.isEmpty(data)) {
                for (final JSONObject file : data) {
                    final Handler handler = createHandler(server, file);
                    if (null != handler) {
                        list.addHandler(handler);
                    }
                }
            }
        }
        return list;
    }

    private static ContextHandlerCollection initContextHandlers(final WebServer server, final JSONObject endpoints) {
        final ContextHandlerCollection result = new ContextHandlerCollection();
        if (null != endpoints && JsonWrapper.getBoolean(endpoints, PARAM_ENABLED)) {
            // rest
            loadRestHandlers(result, server, JsonWrapper.toListOfJSONObject(JsonWrapper.getArray(endpoints, PARAM_DATA)));
            // servlets
            final ServletContextHandler servlets = initServletHandlers(server, JsonWrapper.getJSON(endpoints, PARAM_SERVLETS));
            if (null != servlets) {
                result.addHandler(servlets);
            }
        }
        return result;
    }

    private static void loadRestHandlers(final ContextHandlerCollection list, final WebServer server, final List<JSONObject> data) {
        if (!CollectionUtils.isEmpty(data)) {
            for (final JSONObject file : data) {
                final Handler handler = createHandler(server, file);
                if (null != handler) {
                    list.addHandler(handler);
                }
            }
        }
    }

    private static Handler createHandler(final WebServer server, final JSONObject file) {
        if (null != file) {
            try {
                final String className = JsonWrapper.getString(file, "class");
                final String[] welcomes = JsonWrapper.toArrayOfString(JsonWrapper.getArray(file, "welcome"));
                final String contextPath = JsonWrapper.getString(file, "endpoint");
                final Object params = JsonWrapper.get(file, "params");
                if (StringUtils.hasText(className)) {
                    final Class handlerClass = ClassLoaderUtils.forName(className);
                    if (null == handlerClass) {
                        throw new Exception(FormatUtils.format("Class not found '{0}'", className));
                    }
                    final Object instance = null != params
                            ? ClassLoaderUtils.newInstance(handlerClass, new Class[]{Object.class}, new Object[]{params})
                            : ClassLoaderUtils.newInstance(handlerClass);
                    if (instance instanceof Handler) {
                        // server
                        BeanUtils.setValueIfAny(instance, "server", server);
                        // doc_root
                        BeanUtils.setValueIfAny(instance, "resourceBase", server.getRoot());
                        // welcome files
                        if (null != welcomes && welcomes.length > 0) {
                            BeanUtils.setValueIfAny(instance, "welcomeFiles", welcomes);
                        }
                        // contextPath for context handlers
                        if (StringUtils.hasText(contextPath)) {
                            BeanUtils.setValueIfAny(instance, "contextPath", contextPath);
                        }
                        return (Handler) instance;
                    }
                }
            } catch (Throwable t) {
                staticLogger().log(Level.SEVERE, null, t);
            }
        }
        return null;
    }

    private static ServletContextHandler initServletHandlers(final WebServer server, final JSONObject servlets) {
        if (null != servlets && JsonWrapper.getBoolean(servlets, PARAM_ENABLED)) {
            // creates context
            final String contextPath = JsonWrapper.getString(servlets, PARAM_ENDPOINT, "/");
            final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath(contextPath);

            // creates servlets
            final JSONArray servletData = JsonWrapper.getArray(servlets, PARAM_DATA);
            if (null != servletData && servletData.length() > 0) {
                for (int i = 0; i < servletData.length(); i++) {
                    final JSONObject servletJSON = servletData.optJSONObject(i);
                    final Object handler = createServletOrFilter(server, servletJSON);
                    if (null != handler) {
                        final String pathSpec = JsonWrapper.getString(servletJSON, PARAM_ENDPOINT);
                        final boolean isMultipart = JsonWrapper.getBoolean(servletJSON, PARAM_MULTIPART);
                        final String location = server.getWorkSpacePath(JsonWrapper.getString(servletJSON, PARAM_MULTIPART_LOCATION));
                        final long maxFileSize = JsonWrapper.getLong(servletJSON, PARAM_MULTIPART_MAX_FILE, 1048576); // 1Mb
                        final long maxRequestSize = JsonWrapper.getLong(servletJSON, PARAM_MULTIPART_MAX_REQUEST, maxFileSize);
                        final int fileSizeThreshold = JsonWrapper.getInt(servletJSON, PARAM_MULTIPART_FILE_THRESHOLD, 262144);
                        if (handler instanceof Servlet) {
                            // servlet
                            final ServletHolder sh = new ServletHolder((Servlet) handler);
                            if (isMultipart) {
                                final MultipartConfigElement config = new MultipartConfigElement(
                                        location,               // location (String)
                                        maxFileSize,            // maxFileSize (long)
                                        maxRequestSize,         // maxRequestSize (long)
                                        fileSizeThreshold);     // fileSizeThreshold (int)
                                sh.getRegistration().setMultipartConfig(config);
                                sh.getRegistration().setInitParameter(PARAM_MULTIPART_LOCATION, location);
                                sh.getRegistration().setInitParameter(PARAM_MULTIPART_MAX_FILE, maxFileSize + "");
                                sh.getRegistration().setInitParameter(PARAM_MULTIPART_MAX_REQUEST, maxRequestSize + "");
                                sh.getRegistration().setInitParameter(PARAM_MULTIPART_FILE_THRESHOLD, fileSizeThreshold + "");
                            }
                            context.addServlet(sh, pathSpec);
                            server.registerEndPoint(pathSpec);
                        } else if (handler instanceof Filter) {
                            // filter
                            final FilterHolder holder = new FilterHolder((Filter) handler);
                            if (isMultipart) {
                                holder.getRegistration().setInitParameter(PARAM_MULTIPART_LOCATION, location);
                                holder.getRegistration().setInitParameter(PARAM_MULTIPART_MAX_FILE, maxFileSize + "");
                                holder.getRegistration().setInitParameter(PARAM_MULTIPART_MAX_REQUEST, maxRequestSize + "");
                                holder.getRegistration().setInitParameter(PARAM_MULTIPART_FILE_THRESHOLD, fileSizeThreshold + "");
                                holder.getRegistration().setInitParameter("deleteFiles", "true");
                                holder.getRegistration().setInitParameter("fileOutputBuffer", fileSizeThreshold + "");
                                holder.getRegistration().setInitParameter("maxFileSize", maxFileSize + "");
                                holder.getRegistration().setInitParameter("maxRequestSize", maxRequestSize + "");
                                holder.getRegistration().setInitParameter("maxFormKeys", "1000");
                            }
                            context.addFilter(holder, pathSpec, EnumSet.allOf(DispatcherType.class));
                            server.registerEndPoint(pathSpec);
                        } else {
                            staticLogger().log(Level.WARNING, FormatUtils.format("UNSUPPORTED HANDLER. " +
                                    "Only Servlets and Filter are admitted. Handler '{0}' is not supported " +
                                    "and will not be installed on path '{1}'.",
                                    handler.getClass().getName(), pathSpec));
                        }
                    }
                }
            }

            return context;
        }
        return null;
    }

    private static Object createServletOrFilter(final WebServer server, final JSONObject servlet) {
        if (null != servlet) {
            try {
                final String className = JsonWrapper.getString(servlet, "class");
                if (StringUtils.hasText(className)) {
                    final Class servletClass = ClassLoaderUtils.forName(className);
                    if (null != servletClass) {
                        final Object params = JsonWrapper.get(servlet, "params");
                        final Object instance;
                        if (null != params && StringUtils.hasText(params.toString())) {
                            instance = newInstance(servletClass, params);
                        } else {
                            instance = newInstance(servletClass, servlet);
                        }
                        // server
                        BeanUtils.setValueIfAny(instance, "server", server);
                        // doc_root
                        BeanUtils.setValueIfAny(instance, "resourceBase", server.getRoot());

                        return instance;
                    } else {
                        staticLogger().log(Level.WARNING, FormatUtils.format("Servlet not found: '{0}'", className));
                    }
                }
            } catch (Throwable t) {
                staticLogger().log(Level.SEVERE, null, t);
            }
        }
        return null;
    }

    private static Object newInstance(final Class aclass, final Object params) throws Exception {
        try {
            return ClassLoaderUtils.newInstance(aclass, new Class[]{Object.class}, new Object[]{params});
        } catch (Throwable ignored) {
            return ClassLoaderUtils.newInstance(aclass);
        }
    }

    private static FilterHolder createFilter(final WebServer server, final JSONObject filter) {
        if (null != filter) {
            try {
                final String className = JsonWrapper.getString(filter, "class");
                if (StringUtils.hasText(className)) {
                    final Class servletClass = ClassLoaderUtils.forName(className);
                    if (null != servletClass) {
                        final Object params = JsonWrapper.get(filter, "params");
                        final Object instance;
                        if (null != params) {
                            instance = ClassLoaderUtils.newInstance(servletClass, new Class[]{Object.class}, new Object[]{params});
                        } else {
                            instance = ClassLoaderUtils.newInstance(servletClass);
                        }
                        if (instance instanceof Servlet) {
                            // doc_root
                            BeanUtils.setValueIfAny(instance, "resourceBase", server.getRoot());
                            return new FilterHolder((Filter) instance);
                        }
                    } else {
                        staticLogger().log(Level.WARNING, FormatUtils.format("Servlet not found: '{0}'", className));
                    }
                }
            } catch (Throwable t) {
                staticLogger().log(Level.SEVERE, null, t);
            }
        }
        return null;
    }

    // --------------------------------------------------------------------
    //               L A U N C H E R
    // --------------------------------------------------------------------

    public static boolean isEnabled(){
        return Smartly.getConfiguration().getBoolean("http.webserver.enabled");
    }

    public static WebServer create() throws IOException {
        final JSONObject configuration = Smartly.getConfiguration().getJSONObject("http.webserver");
        final String docRoot = JsonWrapper.getString(configuration, "root");
        final String absoluteDocRoot = Smartly.getAbsolutePath(docRoot);

        // ensure resource base exists
        FileUtils.mkdirs(absoluteDocRoot);

        //-- start the web server --//
        return new WebServer(absoluteDocRoot, configuration);
    }

    public static WebServer launch(final boolean join) throws IOException {
        if (isEnabled()) {
            //-- start the web server --//
            final WebServer server = create();
            server.start(join);
            return server;
        }
        return null;
    }
}
