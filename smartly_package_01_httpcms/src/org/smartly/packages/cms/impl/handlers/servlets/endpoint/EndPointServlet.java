package org.smartly.packages.cms.impl.handlers.servlets.endpoint;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.util.*;
import org.smartly.packages.cms.SmartlyHttpCms;
import org.smartly.packages.cms.impl.cms.endpoint.CMSEndPointPage;
import org.smartly.packages.cms.impl.cms.endpoint.CMSRouter;
import org.smartly.packages.http.impl.WebServer;
import org.smartly.packages.http.impl.util.ServletUtils;
import org.smartly.packages.http.impl.util.vtool.Cookies;
import org.smartly.packages.http.impl.util.vtool.Req;
import org.smartly.packages.velocity.impl.VLCManager;
import org.smartly.packages.velocity.impl.vtools.util.VLCToolbox;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * Servlet for site file parsing.
 * <p/>
 * This servlet replace SmartlyVHTMLServlet
 */
public class EndPointServlet
        extends HttpServlet {

    public static String PATH = "/*";
    private static final String MIME_HTML = "text/html";

    private static final Set<String> _extensions = new HashSet<String>(Arrays.asList(new String[]{".vhtml"}));

    private Resource _baseResource;
    private WebServer _server;

    public EndPointServlet() {
    }

    public EndPointServlet(final Object params) {
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void setServer(final WebServer server) {
        _server = server;
        _server.getServletExtensions().addAll(_extensions);
    }

    public void setBaseResource(final Resource base) {
        _baseResource = base;
    }

    public void setResourceBase(final String resourceBase) {
        try {
            this.setBaseResource(Resource.newResource(resourceBase));
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
            throw new IllegalArgumentException(resourceBase);
        }
    }

    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        this.handle(request, response);
    }

    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        this.handle(request, response);
    }

    protected void handle(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        this.handleInternal(request, response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return SmartlyHttpCms.getCMSLogger();
    }

    private void handleInternal(final HttpServletRequest request,
                                final HttpServletResponse response) throws ServletException, IOException {
        final String resourcePath = ServletUtils.getResourcePath(request);
        final CMSRouter cms = SmartlyHttpCms.getCMS();

        if (cms.contains(resourcePath)) {
            //-- CMS --//
            final CMSEndPointPage page = cms.getPage(resourcePath);
            final String template = cms.getPageTemplate(resourcePath);
            final Map<String, String> params = cms.getUrlParams(resourcePath);

            if (null == page || !StringUtils.hasText(template)) {
                ServletUtils.notFound404(response);
                return;
            }

            // eval template
            final byte[] output = this.merge(template, page, params, request, response);

            // write body
            ServletUtils.writeResponse(response, DateUtils.now().getTime(), MIME_HTML, output);
            return;
        } else if (isVelocity(resourcePath)) {
            //-- .vhtml --//
            final Resource resource = ServletUtils.getResource(_baseResource, null, resourcePath);

            if (!resource.exists()) {
                ServletUtils.notFound404(response);
                return;
            }

            // parse resource
            final byte[] output = this.merge(resource, request, response);

            // write body
            ServletUtils.writeResponse(response, DateUtils.now().getTime(), MIME_HTML, output);
            return;
        }

        ServletUtils.notFound404(response);
    }


    private byte[] merge(final String templateText,
                         final CMSEndPointPage page,
                         final Map<String, String> restParams,
                         final HttpServletRequest request,
                         final HttpServletResponse response) {
        try {
            // session context
            final HttpSession session = request.getSession(true);
            if (session.isNew()) {
                session.setAttribute("velocity-context", new HashMap<String, Object>());
            }

            final Map<String, Object> sessionContext = (Map<String, Object>) session.getAttribute("velocity-context");
            final VelocityEngine engine = getEngine();

            // execution context
            final VelocityContext context = new VelocityContext(sessionContext, this.createInnerContext(
                    page.getUrl(), restParams, request, response));

            // creates new context page
            final CMSEndPointPage ctxPage = new CMSEndPointPage(page, engine, context);

            context.put(CMSEndPointPage.NAME, ctxPage);

            //-- eval velocity template --//
            final String result;
            if (null != engine) {
                result = VLCManager.getInstance().evaluateText(engine, ctxPage.getUrl(), templateText, context);
            } else {
                result = VLCManager.getInstance().evaluateText(ctxPage.getUrl(), templateText, context);
            }
            if (StringUtils.hasText(result)) {
                return result.getBytes();
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, FormatUtils.format(
                    "ERROR MERGING TEMPLATE FOR RESOURCE '{0}': {1}",
                    page.getUrl(), ExceptionUtils.getRealMessage(t)), t);
        }
        return new byte[0];
    }

    private byte[] merge(final Resource resource,
                         final HttpServletRequest request,
                         final HttpServletResponse response) {
        try {
            // session context
            final HttpSession session = request.getSession(true);
            if (session.isNew()) {
                session.setAttribute("velocity-context", new HashMap<String, Object>());
            }

            final Map<String, Object> sessionContext = (Map<String, Object>) session.getAttribute("velocity-context");
            final VelocityEngine engine = getEngine();

            // execution context
            final VelocityContext context = new VelocityContext(sessionContext, this.createInnerContext(
                    resource.getName(), null, request, response));

            //-- eval velocity template --//
            final String text = new String(ByteUtils.getBytes(resource.getInputStream()), Smartly.getCharset());
            final String result;
            if (null != engine) {
                result = VLCManager.getInstance().evaluateText(engine, resource.getName(), text, context);
            } else {
                result = VLCManager.getInstance().evaluateText(resource.getName(), text, context);
            }
            if (StringUtils.hasText(result)) {
                return result.getBytes();
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, FormatUtils.format(
                    "ERROR MERGING TEMPLATE FOR RESOURCE '{0}': {1}",
                    resource.getName(), ExceptionUtils.getRealMessage(t)), t);
        }
        return new byte[0];
    }

    private VelocityContext createInnerContext(final String url,
                                               final Map<String, String> restParams,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {
        final VelocityContext result = new VelocityContext(VLCToolbox.getInstance().getToolsContext());

        //-- "$req" tool --//
        result.put(Req.NAME, new Req(url, restParams, request, response));

        //-- "$cookies" tool --//
        result.put(Cookies.NAME, new Cookies(request, response));

        return result;
    }


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static VelocityEngine __engine;

    private static VelocityEngine getEngine() throws Exception {
        if (null == __engine) {
            __engine = VLCManager.getInstance().getEngine().getNativeEngine();
        }
        return __engine;
    }

    private static boolean isVelocity(final String path) {
        final String ext = PathUtils.getFilenameExtension(path, true);
        return _extensions.contains(ext);
    }
}
