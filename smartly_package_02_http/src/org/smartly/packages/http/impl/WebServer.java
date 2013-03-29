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
import javax.servlet.Servlet;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Web Server Wrapper
 *
 * More about Jetty here:
 * http://git.eclipse.org/c/jetty/org.eclipse.jetty.project.git/tree/examples/embedded/src/main/java/org/eclipse/jetty/embedded/
 */
public class WebServer extends AbstractHttpServer {


    public WebServer(final String docRoot, final JSONObject configuration) {
        super(docRoot, configuration);
    }

    @Override
    public void start() throws Exception {
        try {
            final String jettyHome = super.getRoot(); // absolute path
            final JSONObject configuration = super.getConfiguration();


            //-- init connectors --//
            final Connector[] connectors = initConnectors(super.getJetty(), jettyHome, configuration);

            super.getJetty().setConnectors(connectors);

            //-- init handlers --//
            final Handler handler = initHandlers(this, configuration);
            if (null != handler) {
                super.getJetty().setHandler(handler);
            }

            // init velocity engine
            initVelocity(jettyHome);

            if (!Smartly.isTestUnitMode()) {
                //-- start jetty --//
                super.getJetty().start();
                super.getJetty().join();
            }
        } catch (Throwable t) {
            super.error(t);
        }
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

    private static Connector[] initConnectors(final Server server, final String jettyHome, final JSONObject configuration) {
        final JSONObject connectors = JsonWrapper.getJSON(configuration, "connectors");
        final Iterator<String> keys = connectors.keys();
        final List<Connector> result = new LinkedList<Connector>();

        // http configuration
        final HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);

        while (keys.hasNext()) {
            final String key = keys.next();

            if ("http".equalsIgnoreCase(key)) {
                final JSONObject connector = JsonWrapper.getJSON(connectors, key);
                if (null != connector && JsonWrapper.getBoolean(connector, "enabled")) {

                    http_config.setRequestHeaderSize(JsonWrapper.getInt(connector, "header_buffer", 8112));

                    final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
                    http.setPort(JsonWrapper.getInt(connector, "port", 8080));
                    http.setIdleTimeout(JsonWrapper.getInt(connector, "max_idle", 30000));
                    result.add(http);
                }
            } else if ("ssl".equalsIgnoreCase(key)) {
                final JSONObject connector = JsonWrapper.getJSON(connectors, key);
                if (null != connector && JsonWrapper.getBoolean(connector, "enabled")) {

                    final String keySorePath = mkdirs(PathUtils.join(jettyHome, "/etc/keystore"));

                    // SSL Context Factory for HTTPS and SPDY
                    SslContextFactory sslContextFactory = new SslContextFactory();
                    sslContextFactory.setKeyStorePath(keySorePath);
                    sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
                    sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

                    // HTTPS Configuration
                    HttpConfiguration https_config = new HttpConfiguration(http_config);
                    https_config.addCustomizer(new SecureRequestCustomizer());

                    // HTTPS connector
                    ServerConnector https = new ServerConnector(server,
                            new SslConnectionFactory(sslContextFactory,"http/1.1"),
                            new HttpConnectionFactory(https_config));
                    https.setPort(JsonWrapper.getInt(connector, "port", 8443));

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

        final JSONObject handlers = JsonWrapper.getJSON(configuration, "handlers");
        if (null != handlers && handlers.length() > 0) {

            //-- file handler --//
            final HandlerList main = initChainHandlers(server, JsonWrapper.getJSON(handlers, "chain"));

            //-- shutdown handler --//
            main.addHandler(new SmartlyShutdownHandler(server.getJetty(), shutdownToken));

            //-- add endpoints (servlets and rest) --//
            final ContextHandlerCollection endpoints = initContextHandlers(server, JsonWrapper.getJSON(handlers, "endpoints"));
            if (null != endpoints) {
                main.addHandler(endpoints);
            }

            return main;
        }

        return null;
    }

    private static HandlerList initChainHandlers(final WebServer server, final JSONObject chain) {
        final HandlerList list = new HandlerList();
        if (null != chain && JsonWrapper.getBoolean(chain, "enabled")) {
            final List<JSONObject> data = JsonWrapper.toListOfJSONObject(JsonWrapper.getArray(chain, "data"));
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
        if (null != endpoints && JsonWrapper.getBoolean(endpoints, "enabled")) {
            // rest
            loadRestHandlers(result, server, JsonWrapper.toListOfJSONObject(JsonWrapper.getArray(endpoints, "data")));
            // servlets
            final ServletContextHandler servlets = initServletHandlers(server, JsonWrapper.getJSON(endpoints, "servlets"));
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
        if (null != servlets && JsonWrapper.getBoolean(servlets, "enabled")) {
            // creates context
            final String contextPath = JsonWrapper.getString(servlets, "endpoint", "/");
            final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath(contextPath);

            // creates servlets
            final JSONArray servletData = JsonWrapper.getArray(servlets, "data");
            if (null != servletData && servletData.length() > 0) {
                for (int i = 0; i < servletData.length(); i++) {
                    final JSONObject servletJSON = servletData.optJSONObject(i);
                    final Object handler = createServlet(server, servletJSON);
                    if (null != handler) {
                        final String pathSpec = JsonWrapper.getString(servletJSON, "endpoint");
                        if (handler instanceof Servlet) {
                            context.addServlet(new ServletHolder((Servlet) handler), pathSpec);
                            server.registerEndPoint(pathSpec);
                        } else if (handler instanceof Filter) {
                            context.addFilter(new FilterHolder((Filter) handler), pathSpec, EnumSet.allOf(DispatcherType.class));
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

    private static Object createServlet(final WebServer server, final JSONObject servlet) {
        if (null != servlet) {
            try {
                final String className = JsonWrapper.getString(servlet, "class");
                if (StringUtils.hasText(className)) {
                    final Class servletClass = ClassLoaderUtils.forName(className);
                    if (null != servletClass) {
                        final Object params = JsonWrapper.get(servlet, "params");
                        final Object instance;
                        if (null != params) {
                            instance = ClassLoaderUtils.newInstance(servletClass, new Class[]{Object.class}, new Object[]{params});
                        } else {
                            instance = ClassLoaderUtils.newInstance(servletClass);
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
}
