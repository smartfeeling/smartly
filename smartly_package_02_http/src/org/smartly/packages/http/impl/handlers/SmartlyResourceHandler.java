package org.smartly.packages.http.impl.handlers;

import org.apache.velocity.VelocityContext;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.http.impl.util.ServletUtils;
import org.smartly.packages.http.impl.util.resource.MemoryResource;
import org.smartly.packages.http.impl.util.vtool.Cookies;
import org.smartly.packages.http.impl.util.vtool.Req;
import org.smartly.packages.velocity.impl.VLCManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: angelo.geminiani
 */
public class SmartlyResourceHandler extends HandlerWrapper {

    private static final String MIME_HTML = "text/html";
    private static final Set<String> _velocityExtensions = new HashSet<String>(Arrays.asList(".vhtml"));

    ContextHandler _context;
    Resource _baseResource;
    Resource _defaultStylesheet;
    Resource _stylesheet;
    String[] _welcomeFiles = {"index.html"};
    MimeTypes _mimeTypes = new MimeTypes();
    ByteArrayBuffer _cacheControl;
    boolean _aliases;
    boolean _directory;

    /* ------------------------------------------------------------ */
    public SmartlyResourceHandler() {

    }

    /* ------------------------------------------------------------ */
    public MimeTypes getMimeTypes() {
        return _mimeTypes;
    }

    /* ------------------------------------------------------------ */
    public void setMimeTypes(MimeTypes mimeTypes) {
        _mimeTypes = mimeTypes;
    }

    /* ------------------------------------------------------------ */

    /**
     * @return True if resource aliases are allowed.
     */
    public boolean isAliases() {
        return _aliases;
    }

    /* ------------------------------------------------------------ */

    /**
     * Set if resource aliases (eg symlink, 8.3 names, case insensitivity) are allowed.
     * Allowing aliases can significantly increase security vulnerabilities.
     * If this handler is deployed inside a ContextHandler, then the
     * {@link ContextHandler#isAliases()} takes precedent.
     *
     * @param aliases True if aliases are supported.
     */
    public void setAliases(boolean aliases) {
        _aliases = aliases;
    }

    /* ------------------------------------------------------------ */

    /**
     * Get the directory option.
     *
     * @return true if directories are listed.
     */
    public boolean isDirectoriesListed() {
        return _directory;
    }

    /* ------------------------------------------------------------ */

    /**
     * Set the directory.
     *
     * @param directory true if directories are listed.
     */
    public void setDirectoriesListed(boolean directory) {
        _directory = directory;
    }

    /* ------------------------------------------------------------ */
    @Override
    public void doStart()
            throws Exception {
        ContextHandler.Context scontext = ContextHandler.getCurrentContext();
        _context = (scontext == null ? null : scontext.getContextHandler());

        if (_context != null)
            _aliases = _context.isAliases();

        if (!_aliases && !FileResource.getCheckAliases())
            throw new IllegalStateException("Alias checking disabled");

        super.doStart();
    }

    /* ------------------------------------------------------------ */

    /**
     * @return Returns the resourceBase.
     */
    public Resource getBaseResource() {
        if (_baseResource == null)
            return null;
        return _baseResource;
    }

    /* ------------------------------------------------------------ */

    /**
     * @return Returns the base resource as a string.
     */
    public String getResourceBase() {
        if (_baseResource == null)
            return null;
        return _baseResource.toString();
    }


    /* ------------------------------------------------------------ */

    /**
     * @param base The resourceBase to set.
     */
    public void setBaseResource(Resource base) {
        _baseResource = base;
    }

    /* ------------------------------------------------------------ */

    /**
     * @param resourceBase The base resource as a string.
     */
    public void setResourceBase(String resourceBase) {
        try {
            setBaseResource(Resource.newResource(resourceBase));
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
            throw new IllegalArgumentException(resourceBase);
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * @return Returns the stylesheet as a Resource.
     */
    public Resource getStylesheet() {
        if (_stylesheet != null) {
            return _stylesheet;
        } else {
            if (_defaultStylesheet == null) {
                try {
                    _defaultStylesheet = Resource.newResource(this.getClass().getResource("/jetty-dir.css"));
                } catch (IOException e) {
                    this.getLogger().warning(e.toString());
                }
            }
            return _defaultStylesheet;
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * @param stylesheet The location of the stylesheet to be used as a String.
     */
    public void setStylesheet(String stylesheet) {
        try {
            _stylesheet = Resource.newResource(stylesheet);
            if (!_stylesheet.exists()) {
                this.getLogger().warning("unable to find custom stylesheet: " + stylesheet);
                _stylesheet = null;
            }
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
            throw new IllegalArgumentException(stylesheet.toString());
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * @return the cacheControl header to set on all static content.
     */
    public String getCacheControl() {
        return _cacheControl.toString();
    }

    /* ------------------------------------------------------------ */

    /**
     * @param cacheControl the cacheControl header to set on all static content.
     */
    public void setCacheControl(String cacheControl) {
        _cacheControl = cacheControl == null ? null : new ByteArrayBuffer(cacheControl);
    }

    /* ------------------------------------------------------------ */
    /*
     */
    public Resource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/"))
            throw new MalformedURLException(path);

        Resource base = _baseResource;
        if (base == null) {
            if (_context == null)
                return null;
            base = _context.getBaseResource();
            if (base == null)
                return null;
        }

        try {
            path = URIUtil.canonicalPath(path);
            return base.addPath(path);
        } catch (Exception ignore) {
        }

        return null;
    }

    /* ------------------------------------------------------------ */
    protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
        String servletPath;
        String pathInfo;
        Boolean included = request.getAttribute(Dispatcher.INCLUDE_REQUEST_URI) != null;
        if (included != null && included.booleanValue()) {
            servletPath = (String) request.getAttribute(Dispatcher.INCLUDE_SERVLET_PATH);
            pathInfo = (String) request.getAttribute(Dispatcher.INCLUDE_PATH_INFO);

            if (servletPath == null && pathInfo == null) {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        } else {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
        }

        String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        return getResource(pathInContext);
    }


    /* ------------------------------------------------------------ */
    public String[] getWelcomeFiles() {
        return _welcomeFiles;
    }

    /* ------------------------------------------------------------ */
    public void setWelcomeFiles(String[] welcomeFiles) {
        _welcomeFiles = welcomeFiles;
    }

    /* ------------------------------------------------------------ */
    protected Resource getWelcome(Resource directory) throws MalformedURLException, IOException {
        for (int i = 0; i < _welcomeFiles.length; i++) {
            Resource welcome = directory.addPath(_welcomeFiles[i]);
            if (welcome.exists() && !welcome.isDirectory())
                return welcome;
        }

        return null;
    }

    /* ------------------------------------------------------------ */
    /*
     * @see org.eclipse.jetty.server.Handler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled())
            return;

        boolean skipContentBody = false;

        if (!HttpMethods.GET.equals(request.getMethod())) {
            if (!HttpMethods.HEAD.equals(request.getMethod())) {
                //try another handler
                super.handle(target, baseRequest, request, response);
                return;
            }
            skipContentBody = true;
        }

        Resource resource = this.getResource(request);

        //-- is css request?--//
        if (resource == null || !resource.exists()) {
            if (target.endsWith("/jetty-dir.css")) {
                resource = getStylesheet();
                if (resource == null)
                    return;
                response.setContentType("text/css");
            } else {
                //no resource - try other handlers
                super.handle(target, baseRequest, request, response);
                return;
            }
        }

        //-- is Alias? --//
        if (!_aliases && resource.getAlias() != null) {
            this.getLogger().info(resource + " aliased to " + resource.getAlias());
            return;
        }

        // We are going to serve something
        baseRequest.setHandled(true);

        if (resource.isDirectory()) {
            if (!request.getPathInfo().endsWith(URIUtil.SLASH)) {
                response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getRequestURI(), URIUtil.SLASH)));
                return;
            }

            Resource welcome = getWelcome(resource);
            if (welcome != null && welcome.exists())
                resource = welcome;
            else {
                doDirectory(request, response, resource);
                baseRequest.setHandled(true);
                return;
            }
        }

        final boolean isvelocity = this.isVelocity(resource.getName());

        // set some headers
        final long last_modified = isvelocity ? DateUtils.now().getTime() : resource.lastModified();
        if (!isvelocity && last_modified > 0) {
            long if_modified = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
            if (if_modified > 0 && last_modified / 1000 <= if_modified / 1000) {
                response.setStatus(HttpStatus.NOT_MODIFIED_304);
                return;
            }
        }

        final String mimeType = isvelocity ? MIME_HTML : this.getMimeType(resource, request);

        // set the headers
        this.doResponseHeaders(response, resource, mimeType != null ? mimeType : null);

        response.setDateHeader(HttpHeaders.LAST_MODIFIED, last_modified);

        if (skipContentBody)
            return;

        // Send the content
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }

        //-- if velocity, merge resource with template --//
        final Resource outResource = isvelocity ? this.merge(resource, request, response) :resource;

        // See if a short direct method can be used?
        if (out instanceof AbstractHttpConnection.Output) {
            ((AbstractHttpConnection.Output) out).sendContent(outResource.getInputStream());
        } else {
            // Write content normally
            resource.writeTo(out, 0, outResource.length());
        }
    }

    /* ------------------------------------------------------------ */
    protected void doDirectory(final HttpServletRequest request, final HttpServletResponse response, final Resource resource)
            throws IOException {
        if (_directory) {
            final String listing = resource.getListHTML(request.getRequestURI(), request.getPathInfo().lastIndexOf("/") > 0);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println(listing);
        } else
            response.sendError(HttpStatus.FORBIDDEN_403);
    }

    /* ------------------------------------------------------------ */

    /**
     * Set the response headers.
     * This method is called to set the response headers such as content type and content length.
     * May be extended to add additional headers.
     *
     * @param response
     * @param resource
     * @param mimeType
     */
    protected void doResponseHeaders(HttpServletResponse response, Resource resource, String mimeType) {
        ServletUtils.doResponseHeaders(response, _cacheControl, resource, mimeType);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private String getMimeType(final Resource resource, final HttpServletRequest request){
        Buffer mime = _mimeTypes.getMimeByExtension(resource.toString());
        if (mime == null)
            mime = _mimeTypes.getMimeByExtension(request.getPathInfo());
        return mime.toString();
    }

    private boolean isVelocity(final String target) {
        final String ext = PathUtils.getFilenameExtension(target, true);
        return _velocityExtensions.contains(ext);
    }

    /**
     * Merge resource content with solved velocity template.
     *
     * @param resource
     */
    private Resource merge(final Resource resource, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            //-- creates context data for velocity engine --//
            final VelocityContext vcontext = this.createVelocityContext(resource, request, response);

            //-- eval velocity template --//
            final String text = new String(ByteUtils.getBytes(resource.getInputStream()), Smartly.getCharset());
            final String result = VLCManager.getInstance().evaluateText(resource.getName(), text, vcontext);

            if (StringUtils.hasText(result)) {
                return new MemoryResource(resource.getName(), result.getBytes());
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, FormatUtils.format(
                    "ERROR MERGING TEMPLATE FOR RESOURCE '{0}': {1}",
                    resource.getName(), ExceptionUtils.getRealMessage(t)), t);
        }
        return resource;
    }

    private VelocityContext  createVelocityContext(final Resource resource, final HttpServletRequest request, final HttpServletResponse response){
        final VelocityContext result = new VelocityContext();

        //-- "$req" tool --//
        result.put(Req.NAME, new Req(resource, request, response));

        //-- "$cookies" tool --//
        result.put(Cookies.NAME, new Cookies(resource, request, response));

        return result;
    }
}