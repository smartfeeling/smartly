package org.smartly.packages.http.impl.handlers.servlets;

import org.smartly.commons.io.FileMeta;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.DateUtils;
import org.smartly.packages.http.impl.util.MultipartUtils;
import org.smartly.packages.http.impl.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.List;

/**
 * Servlet for file upload
 */
@MultipartConfig
public class SmartlyUploadServlet
        extends HttpServlet {

    private static final String MIME_HTML = "text/html";


    public SmartlyUploadServlet() {

    }

    public SmartlyUploadServlet(final Object params) {

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


        final List<FileMeta> files = MultipartUtils.getFiles(request);
        response.setContentType("application/json");
        //response.setContentType("text/html;charset=UTF-8");

        final Part filePart = request.getPart("file");
        InputStream filecontent = filePart.getInputStream();

        Object config = request.getAttribute("org.eclipse.jetty.multipartConfig");
        // Create path components to save the file
        final String path = request.getParameter("destination");


        final String fileName = ""; //getFileName(filePart);

        OutputStream out = null;

        try {
            out = new FileOutputStream(new File(path + File.separator
                    + fileName));
            filecontent = filePart.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

        } catch (FileNotFoundException fne) {

            getLogger().log(Level.SEVERE, "Problems during file upload. Error: {0}",
                    new Object[]{fne.getMessage()});
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
        }

        // parse resource
        final byte[] output = "done".getBytes();

        // write body
        ServletUtils.writeResponse(response, DateUtils.now().getTime(), MIME_HTML, output);
    }


}
