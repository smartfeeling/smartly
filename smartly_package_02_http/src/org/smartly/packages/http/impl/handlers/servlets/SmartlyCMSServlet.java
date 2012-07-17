package org.smartly.packages.http.impl.handlers.servlets;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.ExceptionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.http.SmartlyHttp;
import org.smartly.packages.http.impl.htsite.SmartlyCMS;
import org.smartly.packages.http.impl.htsite.SmartlyCMSPage;
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
 * Servlet for site file parsing.
 */
public class SmartlyCMSServlet
        extends HttpServlet {

    public static String PATH = "/*";
    private static final String MIME_HTML = "text/html";

    private Resource _baseResource;

    public SmartlyCMSServlet() {

    }

    public SmartlyCMSServlet(final Object params) {

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
        final SmartlyCMS cms = SmartlyHttp.getCMS();
        if (cms.contains(resourcePath)) {

            final SmartlyCMSPage page = cms.getPage(resourcePath);
            final String template = cms.getPageTemplate(resourcePath);
            if (null == page || !StringUtils.hasText(template)) {
                response.sendError(HttpStatus.NOT_FOUND_404);
                return;
            }

            // parse resource
            final byte[] output = this.merge(template, page, request, response);

            // write body
            ServletUtils.writeResponse(response, DateUtils.now().getTime(), MIME_HTML, output);
        }
    }

    private byte[] merge(final String templateText,
                         final SmartlyCMSPage page,
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
            final VelocityContext context = new VelocityContext(sessionContext, this.createInnerContext(page.getUrl(), request, response));

            // creates new context page
            final SmartlyCMSPage ctxPage = new SmartlyCMSPage(page, engine, context);

            context.put("page", ctxPage);

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



    private VelocityContext createInnerContext(final String url,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {
        final VelocityContext result = new VelocityContext(VLCToolbox.getInstance().getToolsContext());

        //-- "$req" tool --//
        result.put(Req.NAME, new Req(url, request, response));

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
