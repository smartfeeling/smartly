/*
 * 
 */
package org.smartly.packages.htmlparser.impl.vtools;

import org.smartly.Smartly;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.network.URLUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.htmlparser.impl.HtmlParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * HTML Tool.
 *
 * @author angelo.geminiani
 */
public class HtmlTool {

    public static final String NAME = "html";

    private static final String DOCTYPE_HTML5 = "<!DOCTYPE html>";
    private static final String HEAD_OPEN = "<HEAD>";
    private static final String HEAD_CLOSE = "</HEAD>";
    private static final String VIEWPORT = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">";
    private static final String VIEWPORT_TAG = "<meta name=\"viewport\"";

    // ------------------------------------------------------------------------
    //                      Variables
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public HtmlTool() {
    }

    public String getName() {
        return NAME;
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String charset() {
        return getDefaultCharset();
    }

    public InputStream connect(final String uri) {
        try{
            return URLUtils.getInputStream(uri);
        }catch(Throwable t){
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    public HtmlParser open(final String uri) {
        return new HtmlParser(uri);
    }

    /**
     * Returns HTML markup as text
     *
     * @param uri Valid URL
     * @return HTML markup as String
     */
    public String markup(final String uri) {
        final String html = URLUtils.getUrlContent(uri);
        return html;
    }

    public String head(final InputStream is) {
        final HtmlParser parser = new HtmlParser(is);
        return parser.getHead().outerHtml();
    }

    public String head(final InputStream is, final String excludeTags) {
        final HtmlParser parser = new HtmlParser(is);
        final String head = parser.getHead().outerHtml();
        if (StringUtils.hasText(excludeTags)) {
            return HtmlParser.remove(head, StringUtils.split(excludeTags, ","));
        }
        return head;
    }

    public String body(final InputStream is) {
        final HtmlParser parser = new HtmlParser(is);
        return parser.getBody().outerHtml();
    }

    public String body(final InputStream is, final String excludeTags) {
        final HtmlParser parser = new HtmlParser(is);
        final String body = parser.getBody().outerHtml();
        if (StringUtils.hasText(excludeTags)) {
            return HtmlParser.remove(body, StringUtils.split(excludeTags, ","));
        }
        return body;
    }

    public String select(final InputStream is, final String cssQuery) {
        final HtmlParser parser = new HtmlParser(is);
        return parser.selectAsString(cssQuery);
    }

    public String select(final InputStream is, final String cssQuery, final String excludeTags) {
        final HtmlParser parser = new HtmlParser(is);
        final String html_result = parser.selectAsString(cssQuery);
        if (StringUtils.hasText(excludeTags)) {
            return HtmlParser.remove(html_result, StringUtils.split(excludeTags, ","));
        }
        return html_result;
    }

    /**
     * @param html    String
     * @param scripts Text or List. This content will be appended to 'HEAD' tag.
     * @return
     */
    public String head(final String html, final Object scripts) {
        return this.head(html, scripts, true);
    }

    public String head(final String html, final Object contents, final boolean append) {
        // retrieve HTML
        String result = html;
        if (null != contents) {
            int headIndex = -1;
            if (append) {
                headIndex = this.indexOfHeadClose(result);
            } else {
                final String headerTag = this.getHeadOpen(result);
                if (StringUtils.hasText(headerTag)) {
                    headIndex = result.indexOf(headerTag) + headerTag.length();
                }
            }
            if (headIndex > -1) {
                if (contents instanceof String) {
                    result = StringUtils.insert((String) contents, headIndex, result);
                } else if (contents instanceof List) {
                    for (final Object script : (List) contents) {
                        result = StringUtils.insert(script.toString(), headIndex, result);
                        headIndex = this.indexOfHeadClose(result);
                    }
                }
            }
        }
        return result;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String getHeadOpen(final String html) {
        final String lower = html.toLowerCase();
        final int start = lower.indexOf("<head ");
        if (start > -1) {
            final int end = html.indexOf(">", start + 1);
            return html.substring(start, end + 1);
        }
        return "";
    }

    private int indexOfHeadOpen(final String html) {
        final String header = this.getHeadOpen(html);
        if (StringUtils.hasText(header)) {
            return html.indexOf(header);
        }
        return -1;
    }

    private int indexOfHeadClose(final String html) {
        int result = html.indexOf(HEAD_CLOSE.toLowerCase());
        if (result == -1) {
            result = html.indexOf(HEAD_CLOSE);
        }
        return result;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String __CHARSET;

    private String getDefaultCharset() {
        if (null == __CHARSET) {
            final String charset = Smartly.getConfiguration().getString("mobilizer.charset");
            __CHARSET = StringUtils.hasText(charset) && CharEncoding.isSupported(charset) ? charset : CharEncoding.getDefault();
        }
        return __CHARSET;
    }

}
