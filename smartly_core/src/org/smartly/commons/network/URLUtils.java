package org.smartly.commons.network;

import org.smartly.IConstants;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.util.ByteUtils;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.FormatUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * URL shortcut methods.
 */
public class URLUtils {

    private static final String CHARSET = CharEncoding.getDefault();

    public static final String TYPE_JSON = IConstants.TYPE_JSON;
    public static final String TYPE_TEXT = IConstants.TYPE_TEXT;
    public static final String TYPE_HTML = IConstants.TYPE_HTML;

    private URLUtils() {
    }

    public static String getUrlContent(final String uri) {
        return getUrlContent(uri, 3000);
    }

    public static String getUrlContent(final String uri, final String contentType) {
        return getUrlContent(uri, 3000, contentType);
    }

    public static String getUrlContent(final String uri, final int timeout) {
        return getUrlContent(uri, timeout, TYPE_HTML);
    }

    public static String getUrlContent(final String uri, final int timeout, final String contentType) {
        try {
            final InputStream is = getInputStream(uri, timeout, contentType);
            try {
                final byte[] bytes = ByteUtils.getBytes(is);
                return new String(bytes, CHARSET);
            } finally {
                is.close();
            }
        } catch (IOException t) {
            // file not found or connection timeout
            final String resource;
            if (TYPE_HTML.equalsIgnoreCase(contentType)) {
                resource = "error.html";
            } else if (TYPE_JSON.equalsIgnoreCase(contentType)) {
                resource = "error.json";
            } else {
                resource = "error.txt";
            }
            final String text = ClassLoaderUtils.getResourceAsString(null, URLUtils.class, resource, CHARSET);
            final Map<String, String> args = new HashMap<String, String>();
            args.put("uri", uri);
            args.put("error", t.toString());
            return FormatUtils.formatTemplate(text, "{{", "}}", args);
        }
    }

    public static InputStream getInputStream(final String uri) throws IOException {
        return getInputStream(uri, 3000);
    }

    public static InputStream getInputStream(final String uri, final String contentType) throws IOException {
        final URL url = new URL(uri.trim().replaceAll(" ", "+"));
        return getInputStream(url, 3000, contentType);
    }

    public static InputStream getInputStream(final String uri, final int timeout) throws IOException {
        final URL url = new URL(uri.trim().replaceAll(" ", "+"));
        return getInputStream(url, timeout, TYPE_HTML);
    }

    public static InputStream getInputStream(final String uri, final int timeout, final String contentType) throws IOException {
        final URL url = new URL(uri.trim().replaceAll(" ", "+"));
        return getInputStream(url, timeout, contentType);
    }

    public static InputStream getInputStream(final URL url, final int timeout, final String type) throws IOException {
        final Proxy proxy = NetworkUtils.getProxy();
        final URLConnection conn = url.openConnection(proxy);
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).setRequestMethod("GET");
        }
        conn.setConnectTimeout(timeout);
        conn.addRequestProperty("Accept", type);
        conn.connect();

        return conn.getInputStream();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
