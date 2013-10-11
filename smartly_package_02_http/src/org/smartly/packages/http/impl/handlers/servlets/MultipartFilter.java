package org.smartly.packages.http.impl.handlers.servlets;

import org.eclipse.jetty.servlets.MultiPartFilter;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.http.impl.WebServer;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.File;

/* ------------------------------------------------------------ */
/**
 * Multipart Form Data Filter.
 * <p>
 * This class decodes the multipart/form-data stream sent by a HTML form that uses a file input
 * item.  Any files sent are stored to a temporary file and a File object added to the request
 * as an attribute.  All other values are made available via the normal getParameter API and
 * the setCharacterEncoding mechanism is respected when converting bytes to Strings.
 * <p>
 * If the init parameter "delete" is set to "true", any files created will be deleted when the
 * current request returns.
 * <p>
 * The init parameter maxFormKeys sets the maximum number of keys that may be present in a
 * form (default set by system property org.eclipse.jetty.server.Request.maxFormKeys or 1000) to protect
 * against DOS attacks by bad hash keys.
 * <p>
 * The init parameter deleteFiles controls if uploaded files are automatically deleted after the request
 * completes.
 *
 * Use init parameter "maxFileSize" to set the max size file that can be uploaded.
 *
 * Use init parameter "maxRequestSize" to limit the size of the multipart request.
 *
 */
public class MultipartFilter
        extends MultiPartFilter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String location = filterConfig.getInitParameter(WebServer.PARAM_MULTIPART_LOCATION);
        if (StringUtils.hasText(location)) {
            filterConfig.getServletContext().setAttribute("javax.servlet.context.tempdir", new File(location));
        }
        super.init(filterConfig);
    }
}
