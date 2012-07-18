package org.smartly.packages.http.impl.handlers.servlets;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for .vhtml file parsing.
 */
public class SmartlyVHTMLServlet
        extends HttpServlet {

    public static String PATH = "*.vhtml";
    private static final String MIME_HTML = "text/html";

    private Resource _baseResource;

    public SmartlyVHTMLServlet() {

    }

    public SmartlyVHTMLServlet(final Object params) {

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

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void handle(final HttpServletRequest request,
                        final HttpServletResponse response) throws ServletException, IOException {
        final String resourcePath = ServletUtils.getResourcePath(request);
        final Resource resource = ServletUtils.getResource(_baseResource, null, resourcePath);

        if (!resource.exists()) {
            ServletUtils.notFound404(response);
        }

        // parse resource
        final byte[] output = this.merge(resource, request, response);

        // write body
        ServletUtils.writeResponse(response, DateUtils.now().getTime(), MIME_HTML, output);
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

            //-- eval velocity template --//
            final String text = new String(ByteUtils.getBytes(resource.getInputStream()), Smartly.getCharset());
            final String result;
            if (null != engine) {
                result = VLCManager.getInstance().evaluateText(engine, resource.getName(), text,
                        new VelocityContext(sessionContext, this.createInnerContext(resource, request, response)));
            } else {
                result = VLCManager.getInstance().evaluateText(resource.getName(), text,
                        new VelocityContext(sessionContext, this.createInnerContext(resource, request, response)));
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

    private VelocityContext createInnerContext(final Resource resource,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {
        final VelocityContext result = new VelocityContext(VLCToolbox.getInstance().getToolsContext());

        //-- "$req" tool --//
        result.put(Req.NAME, new Req(resource.getName(), request, response));

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


}
