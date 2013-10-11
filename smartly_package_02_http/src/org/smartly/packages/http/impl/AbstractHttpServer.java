/*
 * 
 */

package org.smartly.packages.http.impl;

import org.eclipse.jetty.server.Server;
import org.json.JSONObject;
import org.smartly.IConstants;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.*;

/**
 * @author angelo.geminiani
 */
public abstract class AbstractHttpServer {

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
    private final Map<Class, IHttpHandler> _external_handlers;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public AbstractHttpServer(final String absolutePath,
                              final JSONObject configuration) {
        _absoluteBaseResource = absolutePath;
        _absoluteWorkSpacePath = PathUtils.getParent(_absoluteBaseResource);
        _absoluteEtcPath = PathUtils.concat(_absoluteWorkSpacePath, PATH_ETC);
        _configuration = configuration;
        _external_handlers = Collections.synchronizedMap(new HashMap<Class, IHttpHandler>());

        // init custom log file
        LoggingRepository.getInstance().setLogFileName(this.getClass(), LOG_FILE);

        _jetty = new Server();

        _servletExtensions = new HashSet<String>();
        _servletPaths = new HashSet<String>();
    }

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
            return PathUtils.concat(getRoot(), path);
        }
    }

    public String getWorkSpacePath() {
        return _absoluteWorkSpacePath;
    }

    public String getWorkSpacePath(final String path) {
        if (PathUtils.isAbsolute(path)) {
            return path;
        } else {
            return PathUtils.concat(getWorkSpacePath(), path);
        }
    }

    public String getSslRootPath() {
        return _absoluteEtcPath;
    }

    public void join() throws InterruptedException {
        if (null != _jetty) {
            _jetty.join();
        }
    }

    public void start() throws Exception {
        if (null != _jetty) {
            _jetty.start();
        }
    }

    public void stop() throws Exception {
        if (null != _jetty) {
            _jetty.stop();
        }
    }

    //-- CALLBACKS FOR SERVLET --//

    public void addHandler(final Class servlet, final IHttpHandler handler){
         _external_handlers.put(servlet, handler);
    }

    public boolean handleRequest(final Object sender,
                                 final ServletRequest request,
                                 final ServletResponse response) throws Exception{

        // not handled
        return false;
    }

    //-- LOG --//

    public void debug(final String message) {
        if (this.isDebugMode()) {
            this.getLogger().log(Level.FINE, message);
        }
    }

    public void info(final String message) {
        this.getLogger().log(Level.INFO, message);
    }

    public void error(final String message) {
        this.getLogger().log(Level.SEVERE, message);
    }

    public void error(final String message, final Throwable t) {
        this.getLogger().log(Level.SEVERE, message, t);
    }

    public void error(final Throwable t) {
        this.getLogger().log(Level.SEVERE, null, t);
    }

    // --------------------------------------------------------------------
    //               p r o t e c t e d
    // --------------------------------------------------------------------

    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }


}
