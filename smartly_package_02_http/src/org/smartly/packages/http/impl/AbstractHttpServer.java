/*
 * 
 */

package org.smartly.packages.http.impl;

import org.eclipse.jetty.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.IConstants;
import org.smartly.commons.Delegates;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author angelo.geminiani
 */
public abstract class AbstractHttpServer {

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    public static interface OnError extends Delegates.ExceptionCallback {
    }

    public static interface OnStart {
        void handle(final AbstractHttpServer sender);
    }

    public static interface OnUploaded {
        void handle(final AbstractHttpServer sender, final JSONObject attributes,
                    final JSONObject parameters, final JSONArray files);
    }

    private static final Class EVENT_ON_ERROR = OnError.class;
    private static final Class EVENT_ON_START = OnStart.class;
    private static final Class EVENT_ON_UPLOAD = OnUploaded.class;

    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    private static final String LOG_FILE = IConstants.PATH_LOG.concat("/").concat("http/webserver.log");
    private static final String PATH_ETC = "etc/";

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final String _absoluteBaseResource;
    private final String _absoluteWorkSpacePath;
    private final String _absoluteEtcPath;
    private final JSONObject _configuration;
    private final Server _jetty;
    private final Set<String> _servletExtensions; // resource's extensions managed from servlet (i.e. vhtml)
    private final Set<String> _servletPaths;
    private final Delegates.Handlers _event_handlers;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public AbstractHttpServer(final String absolutePath,
                              final JSONObject configuration) {
        _absoluteBaseResource = absolutePath;
        _absoluteWorkSpacePath = PathUtils.getParent(_absoluteBaseResource);
        _absoluteEtcPath = PathUtils.concat(_absoluteWorkSpacePath, PATH_ETC);
        _configuration = configuration;
        _event_handlers = new Delegates.Handlers();

        // init custom log file
        LoggingRepository.getInstance().setLogFileName(this.getClass(), LOG_FILE);

        _jetty = new Server();

        _servletExtensions = new HashSet<String>();
        _servletPaths = new HashSet<String>();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _event_handlers.clear();
        } catch (Throwable ignore) {
        }
        super.finalize();
    }

    // --------------------------------------------------------------------
    //               e v e n t
    // --------------------------------------------------------------------

    public void onError(final OnError handler) {
        _event_handlers.add(handler);
    }

    public void onStart(final OnStart handler) {
        _event_handlers.add(handler);
    }

    public void onUploaded(final OnUploaded handler) {
        _event_handlers.add(handler);
    }

    public void triggerOnError(final String message, final Throwable t) {
        try {
            if (_event_handlers.contains(EVENT_ON_ERROR)) {
                _event_handlers.triggerAsync(EVENT_ON_ERROR, message, t);
            } else {
                // no  handlers.
            }
        } catch (Throwable ignored) {
        }
    }

    public void triggerOnStart() {
        try {
            if (_event_handlers.contains(EVENT_ON_START)) {
                _event_handlers.triggerAsync(EVENT_ON_START, this);
            } else {
                // no  handlers.
            }
        } catch (Throwable ignored) {
        }
    }

    public void triggerOnUploaded(final JSONObject attributes, final JSONObject parameters, final JSONArray files) {
        try {
            if (_event_handlers.contains(EVENT_ON_UPLOAD)) {
                _event_handlers.triggerAsync(EVENT_ON_UPLOAD, this, attributes, parameters, files);
            } else {
                // no  handlers.
            }
        } catch (Throwable ignored) {
        }
    }
    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    //-- RESOURCE EXTENSIONS MANAGED BY SERVLETS --//

    public void registerEndPoint(final String endPoint) {
        if (endPoint.startsWith("*.")) {
            final String ext = endPoint.substring(1);
            _servletExtensions.add(ext);
        } else {
            final String path = endPoint.replace("*", "");
            _servletPaths.add(path);
        }
    }

    public Set<String> getServletExtensions() {
        return _servletExtensions;
    }

    public Set<String> getServletPaths() {
        return _servletPaths;
    }

    //-- CONFIGURATION --//

    public JSONObject getConfiguration() {
        return _configuration;
    }

    public boolean isDebugMode() {
        return JsonWrapper.getBoolean(_configuration, "debug");
    }

    //-- SERVER --//

    protected Server getJetty() {
        return _jetty;
    }

    /**
     * Returns root path of webserver (the htdoc).
     *
     * @return Absolute path of web server root.
     */
    public String getRoot() {
        return _absoluteBaseResource;
    }

    public String getRootPath(final String path) {
        if (PathUtils.isAbsolute(path)) {
            return path;
        } else {
            return PathUtils.merge(getRoot(), path);
        }
    }

    public String getWorkSpacePath() {
        return _absoluteWorkSpacePath;
    }

    public String getWorkSpacePath(final String path) {
        if (PathUtils.isAbsolute(path)) {
            return path;
        } else {
            return PathUtils.merge(getWorkSpacePath(), path);
        }
    }

    public String getSslRootPath() {
        return _absoluteEtcPath;
    }

    public void start(final boolean join) throws Exception {
        if (null != _jetty) {
            _jetty.start();
            this.triggerOnStart();
            if (join) {
                _jetty.join();
            }
        }
    }

    public void join() throws Exception {
        if (null != _jetty) {
            _jetty.join();
        }
    }

    public void stop() throws Exception {
        if (null != _jetty) {
            _jetty.stop();
        }
    }

    // --------------------------------------------------------------------
    //               p r o t e c t e d
    // --------------------------------------------------------------------

    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    //-- LOG --//

    protected void debug(final String message) {
        if (this.isDebugMode()) {
            this.getLogger().log(Level.FINE, message);
        }
    }

    protected void info(final String message) {
        this.getLogger().log(Level.INFO, message);
    }

    private void error(final String message) {
        this.error(message, null);
    }

    private void error(final Throwable t) {
        this.error(null, t);
    }

    private void error(final String message, final Throwable t) {
        if (null != t) {
            this.getLogger().log(Level.SEVERE, message, t);
        } else {
            this.getLogger().log(Level.SEVERE, message);
        }
    }

}
