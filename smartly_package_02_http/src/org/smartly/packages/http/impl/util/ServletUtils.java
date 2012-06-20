package org.smartly.packages.http.impl.util;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.Smartly;
import org.smartly.commons.util.FileUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 *
 */
public class ServletUtils {

    private ServletUtils() {

    }

    public static void writeResponse(final HttpServletResponse response,
                                     final Long lastModified,
                                     final String content) throws IOException {

        writeResponse(response, lastModified, content.getBytes(Smartly.getCharset()));
    }

    public static void writeResponse(final HttpServletResponse response,
                                     final Long lastModified,
                                     final byte[] content) throws IOException {
        final InputStream is = new ByteArrayInputStream(content);
        writeResponse(response, lastModified, is);
    }

    public static void writeResponse(final HttpServletResponse response,
                                     final Long lastModified,
                                     final Object content) throws IOException {
        if (null != lastModified) {
            response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModified);
        }

        // Send the content
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }

        // See if a short direct method can be used?
        if (out instanceof AbstractHttpConnection.Output) {
            ((AbstractHttpConnection.Output) out).sendContent(content);
        } else if(out instanceof WriterOutputStream){
            // Write content normally
            FileUtils.copy((InputStream)content, out);
        }
    }

    public static void doResponseHeaders(final HttpServletResponse response,
                                         final ByteArrayBuffer cacheControl,
                                         final Resource resource,
                                         final String mimeType) {
        final long length = resource.length();
        doResponseHeaders(response, cacheControl, length, mimeType);
    }

    public static void doResponseHeaders(final HttpServletResponse response,
                                         final ByteArrayBuffer cacheControl,
                                         final long length,
                                         final String mimeType) {
        if (mimeType != null)
            response.setContentType(mimeType);

        if (response instanceof Response) {
            final HttpFields fields = ((Response) response).getHttpFields();

            if (length > 0)
                fields.putLongField(HttpHeaders.CONTENT_LENGTH_BUFFER, length);

            if (cacheControl != null)
                fields.put(HttpHeaders.CACHE_CONTROL_BUFFER, cacheControl);
        } else {
            if (length > 0)
                response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(length));

            if (cacheControl != null)
                response.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl.toString());
        }
    }

}
