package org.smartly.commons.network;

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
import java.util.concurrent.TimeoutException;

/**
 * URL shortcut methods.
 */
public class URLUtils {

    private static final String CHARSET = CharEncoding.getDefault();

    private URLUtils() {
    }

    public static String getUrlContent(final String uri) {
        return getUrlContent(uri, 3000);
    }

    public static String getUrlContent(final String uri, final int timeout) {
        try {
            final InputStream is = getInputStream(uri, timeout);
            try {
                final byte[] bytes = ByteUtils.getBytes(is);
                return new String(bytes, CHARSET);
            } finally {
                is.close();
            }
        } catch (IOException t){
            // file not found or connection timeout
            final String html = ClassLoaderUtils.getResourceAsString(null, URLUtils.class, "error.html", CHARSET);
            return FormatUtils.format(html, uri, t.toString());
        }
    }

    public static InputStream getInputStream(final String uri) throws IOException {
        return getInputStream(uri, 3000);
    }

    public static InputStream getInputStream(final String uri, final int timeout) throws IOException {
        final URL url = new URL(uri.trim().replaceAll(" ", "+"));
        return getInputStream(url, timeout);
    }

    public static InputStream getInputStream(final URL url, final int timeout) throws IOException {
        final Proxy proxy = NetworkUtils.getProxy();
        final URLConnection conn = url.openConnection(proxy);
        if(conn instanceof HttpURLConnection){
           ((HttpURLConnection)conn).setRequestMethod("GET");
        }
        conn.setConnectTimeout(timeout);
        conn.connect();

        return conn.getInputStream();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
