package org.smartly.packages.http.impl.util;

import org.smartly.commons.io.FileMeta;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class MultipartUtils {

    public static List<FileMeta> getFiles(HttpServletRequest request)
            throws IOException, ServletException {

        final List<FileMeta> files = new LinkedList<FileMeta>();
        FileMeta temp = null;
        try {
            // Get all parts and creates meta wrappers
            final Collection<Part> parts = request.getParts();
            for (final Part part : parts) {
                // if part is multiparts "file"
                if (part.getContentType() != null) {

                    // 3.2 Create a new FileMeta object
                    temp = new FileMeta();
                    temp.setFileName(getFilename(part));
                    temp.setFileSize(part.getSize());
                    temp.setFileType(part.getContentType());
                    temp.setContent(part.getInputStream());

                    // 3.3 Add created FileMeta object to List<FileMeta> files
                    files.add(temp);
                }
            }
        } catch (Throwable ignored) {
            final Part part = request.getPart("file");
            if (part.getContentType() != null) {

                // 3.2 Create a new FileMeta object
                temp = new FileMeta();
                temp.setFileName(getFilename(part));
                temp.setFileSize(part.getSize());
                temp.setFileType(part.getContentType());
                temp.setContent(part.getInputStream());

                // 3.3 Add created FileMeta object to List<FileMeta> files
                files.add(temp);
            }
        }
        return files;
    }

    private static String getFilename(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                final String filename = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }

}
