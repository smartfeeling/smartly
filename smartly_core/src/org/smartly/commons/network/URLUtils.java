package org.smartly.commons.network;

import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.util.ByteUtils;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.FormatUtils;

import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * User: angelo.geminiani
 */
public class URLUtils {

    private static final String CHARSET = CharEncoding.getDefault();

    private URLUtils() {
    }

    public static String getUrlContent(final String uri) {
        try {
            final InputStream is = getInputStream(uri);
            try {
                final byte[] bytes = ByteUtils.getBytes(is);
                return new String(bytes, CHARSET);
            } finally {
                is.close();
            }
        } catch (Throwable ignored) {
        }
        // file not found or connection timeout
        final String html = ClassLoaderUtils.getResourceAsString(null, URLUtils.class, "timeout.html", CHARSET);
        return FormatUtils.format(html, uri);
    }

    public static InputStream getInputStream(final String uri) {
        try {
            final URL url = new URL(uri);
            final Proxy proxy = NetworkUtils.getProxy();
            final URLConnection conn = url.openConnection(proxy);
            conn.setConnectTimeout(3000);
            conn.connect();
            return conn.getInputStream();
        } catch (Throwable ignored) {
        }
        // file not found or connection timeout
        return ClassLoaderUtils.getResourceAsStream(null, URLUtils.class, "timeout.html");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
