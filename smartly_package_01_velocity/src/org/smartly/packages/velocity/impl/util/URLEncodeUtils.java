package org.smartly.packages.velocity.impl.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.util.RegExUtils;
import org.smartly.commons.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * User: angelo.geminiani
 */
public class URLEncodeUtils {

    /**
     * Decodes the passed UTF-8 String using an algorithm that's compatible with
     * JavaScript's
     * <code>decodeURIComponent</code> function. Returns
     * <code>null</code> if the String is
     * <code>null</code>.
     *
     * @param s The UTF-8 encoded String to be decoded
     * @return the decoded String
     */
    public static String decodeURI(String s) {
        if (s == null) {
            return null;
        }
        String result;
        try {
            result = URLDecoder.decode(s, "UTF-8");
        } // This exception should never occur.
        catch (Exception e) {
            result = s;
        }
        return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's
     * <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is
     * <code>null</code>.
     *
     * @param s The String to be encoded
     * @return the encoded String
     */
    public static String encodeURI(String s) {
        String result;
        try {
            result = URLEncoder.encode(s, "UTF-8").
                    replaceAll("\\+", "%20").
                    replaceAll("\\%21", "!").
                    replaceAll("\\%27", "'").
                    replaceAll("\\%28", "(").
                    replaceAll("\\%29", ")").
                    replaceAll("\\%7E", "~");
        } // This exception should never occur.
        catch (Exception e) {
            result = s;
        }

        return result;
    }

    public static String encodeHTML(final String text, final char[] exclude) {
        String result = text;
        // creates new array with placeholders
        final String[] exenc;
        if (null != exclude) {
            exenc = new String[exclude.length];
            if (exenc.length > 0) {
                for (int i = 0; i < exclude.length; i++) {
                    final String temp = "_CHAR_" + i;
                    exenc[i] = temp;
                    result = replace(result, exclude[i], temp);
                }
            }
        } else {
            exenc = new String[0];
        }
        // encode fields
        result = StringEscapeUtils.escapeHtml(result);

        // replace placeholders
        if (null != exclude && exenc.length > 0) {
            for (int i = 0; i < exenc.length; i++) {
                final String temp = exenc[i];
                result = result.replaceAll(temp,
                        StringUtils.toString(exclude[i]));
            }
        }

        return result;
    }

    public static String decodeHTML(final String text) {
        return StringEscapeUtils.unescapeHtml(text);
    }

    /**
     * Translates a string into
     * <code>application/x-www-form-urlencoded</code> format using a specific
     * encoding scheme. This method uses the supplied encoding scheme to obtain
     * the bytes for unsafe characters. <p> <em><strong>Note:</strong> The <a
     * href= "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
     * World Wide Web Consortium Recommendation</a> states that UTF-8 should be
     * used. Not doing so may introduce incompatibilites.</em>
     *
     * @param s <code>String</code> to be translated.
     * @return the translated
     *         <code>String</code>.
     * @see URLDecoder#decode(java.lang.String, java.lang.String)
     * @since 1.4
     */
    public static String URLEncoder(final String s) {
        try {
            return URLEncoder.encode(s, CharEncoding.getDefault());
        } catch (Exception ignored) {
        }
        return s;
    }

    /**
     * Decodes a
     * <code>application/x-www-form-urlencoded</code> string using a specific
     * encoding scheme. The supplied encoding is used to determine what
     * characters are represented by any consecutive sequences of the form "
     * <code>%<i>xy</i></code>". <p> <em><strong>Note:</strong> The <a href=
     * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars"> World
     * Wide Web Consortium Recommendation</a> states that UTF-8 should be used.
     * Not doing so may introduce incompatibilites.</em>
     *
     * @param s the
     *          <code>String</code> to decode
     * @return the newly decoded
     *         <code>String</code>
     * @see URLEncoder#encode(java.lang.String, java.lang.String)
     * @since 1.4
     */
    public String URLDecoder(final String s) {
        try {
            return URLDecoder.decode(s, CharEncoding.getDefault());
        } catch (UnsupportedEncodingException ignored) {
        }
        return s;
    }

    public static String URLDecodeWhile(final String s) {
        String result = s;
        try {
            result = URLDecoder.decode(result, CharEncoding.getDefault());
            while (result.contains("%")) {
                try {
                    result = URLDecoder.decode(result, CharEncoding.getDefault());
                } catch (Throwable ignored) {
                    break;
                }
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return result;
    }

    /**
     * Decode all values of map
     *
     * @param map parameters
     */
    public static void URLDecodeWhile(final Map<String, String> map) {
        if (!map.isEmpty()) {
            final Set<String> keys = map.keySet();
            for (final String key : keys) {
                final String value = URLDecodeWhile(map.get(key));
                map.put(key, value);
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static String replace(final String text, final char c, final String val) {
        if (StringUtils.hasText(val)) {
            final String rep = RegExUtils.escape(StringUtils.toString(c));
            return text.replaceAll(rep, val);
        }
        return "";
    }
}
