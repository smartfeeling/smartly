package org.smartly.packages.http.impl.handlers.servlets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.image.resize.Resize;
import org.smartly.commons.io.FileMeta;
import org.smartly.commons.io.FileMetaArray;
import org.smartly.commons.io.FileWrapper;
import org.smartly.commons.util.*;
import org.smartly.packages.http.impl.AbstractHttpServer;
import org.smartly.packages.http.impl.WebServer;
import org.smartly.packages.http.impl.util.ServletUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Servlet for file upload.
 * Works with Jquery-Upload plugin:
 * https://github.com/blueimp/jQuery-File-Upload/wiki
 */
@MultipartConfig
public class SmartlyUploadServlet
        extends HttpServlet {

    private static final String MIME_JSON = "application/json";

    private final JSONObject _params;
    private AbstractHttpServer _server;

    public SmartlyUploadServlet() {
        _params = new JSONObject();
    }

    public SmartlyUploadServlet(final Object params) {
        if (params instanceof JSONObject) {
            _params = (JSONObject) params;
        } else {
            _params = new JSONObject();
        }
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void setServer(final Object server) {
        if (server instanceof AbstractHttpServer) {
            _server = (AbstractHttpServer) server;
        }
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------


    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        final String get_file = request.getParameter("getfile") != null
                ? request.getParameter("getfile") : "";
        final String del_file = request.getParameter("delfile") != null
                ? request.getParameter("delfile") : "";
        final String get_thumb = request.getParameter("getthumb") != null
                ? request.getParameter("getthumb") : "";
        if (StringUtils.hasText(get_file)) {
            final String tempName = getTempFullName(get_file);
            File file = new File(tempName);
            if (file.exists()) {
                int bytes = 0;
                ServletOutputStream op = response.getOutputStream();

                response.setContentType(getMimeType(file));
                response.setContentLength((int) file.length());
                response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

                byte[] bbuf = new byte[1024];
                DataInputStream in = new DataInputStream(new FileInputStream(file));

                while ((in != null) && ((bytes = in.read(bbuf)) != -1)) {
                    op.write(bbuf, 0, bytes);
                }

                in.close();
                op.flush();
                op.close();
            }
        } else if (StringUtils.hasText(del_file)) {
            final String tempName = getTempFullName(del_file);
            File file = new File(tempName);
            if (file.exists()) {
                file.delete(); // TODO:check and report success
            }
        } else if (StringUtils.hasText(get_thumb)) {
            final String tempName = getTempFullName(get_file);
            File file = new File(tempName);
            if (file.exists()) {
                System.out.println(file.getAbsolutePath());
                String mimetype = getMimeType(file);
                if (mimetype.endsWith("png") || mimetype.endsWith("jpeg") || mimetype.endsWith("jpg") || mimetype.endsWith("gif")) {
                    BufferedImage im = ImageIO.read(file);
                    if (im != null) {
                        BufferedImage thumb = Resize.resize(im, 75);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        if (mimetype.endsWith("png")) {
                            ImageIO.write(thumb, "PNG", os);
                            response.setContentType("image/png");
                        } else if (mimetype.endsWith("jpeg")) {
                            ImageIO.write(thumb, "jpg", os);
                            response.setContentType("image/jpeg");
                        } else if (mimetype.endsWith("jpg")) {
                            ImageIO.write(thumb, "jpg", os);
                            response.setContentType("image/jpeg");
                        } else {
                            ImageIO.write(thumb, "GIF", os);
                            response.setContentType("image/gif");
                        }
                        ServletOutputStream srvos = response.getOutputStream();
                        response.setContentLength(os.size());
                        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
                        os.writeTo(srvos);
                        srvos.flush();
                        srvos.close();
                    }
                }
            } // TODO: check and report success
        } else {
            PrintWriter writer = response.getWriter();
            writer.write("call POST with multipart form data");
        }
    }

    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        this.handle(request, response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void handle(final HttpServletRequest request,
                        final HttpServletResponse response) throws ServletException, IOException {
        // get files and parameters
        final FileMetaArray files = ServletUtils.getFiles(request);
        final JSONObject parameters = ServletUtils.getParameters(request);
        final JSONObject attributes = ServletUtils.getAttributes(request);

        OutputStream os = null;
        InputStream is = null;
        File temp_file = null;
        for (final FileMeta file : files) {
            os = null;
            is = file.getContent();
            if (null != is) {
                final String fileName = file.getName();
                final String tempName = GUID.create() + PathUtils.getFilenameExtension(fileName, true);
                final String tempFullName = getTempFullName(tempName);
                try {
                    temp_file = new File(tempFullName);
                    os = new FileOutputStream(new File(tempFullName));
                    int read = 0;
                    final byte[] buffer = new byte[1024];
                    while ((read = is.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                    }
                    file.put("file_wrapper",
                            new FileWrapper(temp_file).setName(fileName));
                    file.put("temp_url", tempFullName);
                    // jquery-Upload response fields
                    file.put("url", getServletUrl() + "?getfile=" + tempName);
                    file.put("thumbnailUrl", getServletUrl() + "?getthumb=" + tempName);
                    file.put("deleteUrl", getServletUrl() + "?delfile=" + tempName);
                    file.put("deleteType", "GET");
                    file.put("thumbnail_url", getServletUrl() + "?getthumb=" + tempName);
                    file.put("delete_url", getServletUrl() + "?delfile=" + tempName);
                    file.put("delete_type", "GET");
                } catch (Throwable t) {
                    this.triggerOnError(FormatUtils.format("Problems during file upload. Error: {0}",
                            t.getMessage()), t);
                } finally {
                    if (os != null) {
                        os.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                }
            }
        }

        // prepare json output
        final byte[] output = this.getResponse(parameters, files);
        // write body
        ServletUtils.writeResponse(response, DateUtils.now().getTime(), MIME_JSON, output);

        // trigger event on server
        this.triggerOnUploaded(attributes, parameters, files.toJSONArray());
    }

    private String getTempFullName(final String name) {
        if (null != _params && null != _server) {
            final String root = _server.getWorkSpacePath(JsonWrapper.getString(_params, WebServer.PARAM_MULTIPART_LOCATION));
            return PathUtils.merge(root, name);
        }
        return "";
    }

    private String getServletUrl() {
        if (null != _params) {
            return JsonWrapper.getString(_params, WebServer.PARAM_ENDPOINT);
        }
        return "";
    }

    private void triggerOnError(final String message, final Throwable t) {
        if (null != _server) {
            _server.triggerOnError(message, t);
        }
    }

    private void triggerOnUploaded(final JSONObject attributes,
                                   final JSONObject parameters,
                                   final JSONArray files) {
        if (null != _server && files.length() > 0) {
            _server.triggerOnUploaded(attributes, parameters, files);
        }
    }

    private String getMimeType(File file) {
        String mimetype = "";
        if (file.exists()) {
            if (getSuffix(file.getName()).equalsIgnoreCase("png")) {
                mimetype = "image/png";
            } else if (getSuffix(file.getName()).equalsIgnoreCase("jpg")) {
                mimetype = "image/jpg";
            } else if (getSuffix(file.getName()).equalsIgnoreCase("jpeg")) {
                mimetype = "image/jpeg";
            } else if (getSuffix(file.getName()).equalsIgnoreCase("gif")) {
                mimetype = "image/gif";
            } else {
                javax.activation.MimetypesFileTypeMap mtMap = new javax.activation.MimetypesFileTypeMap();
                mimetype = mtMap.getContentType(file);
            }
        }
        return mimetype;
    }


    private String getSuffix(String filename) {
        String suffix = "";
        int pos = filename.lastIndexOf('.');
        if (pos > 0 && pos < filename.length() - 1) {
            suffix = filename.substring(pos + 1);
        }
        return suffix;
    }

    private byte[] getResponse(final JSONObject parameters,
                               final FileMetaArray files) {
        final String response = JsonWrapper.getString(parameters, "response");
        if (StringUtils.hasText(response)) {
            return response.getBytes();
        } else {
            return files.toString().getBytes();
        }
    }
}
