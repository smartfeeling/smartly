package org.smartly.packages.htmlparser.impl;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Depends from jsoup HTML parser.
 */
public class HtmlParser {

    private static final String CHARSET = Smartly.getCharset();
    private static final int TIMEOUT = 5000;
    private final Document _document;

    public HtmlParser(final URL url) {
        _document = getDocument(url, TIMEOUT);
    }

    public HtmlParser(final Document document) {
        _document = document;
    }

    public HtmlParser(final String html) {
        _document = getDocument(html, "");
    }

    public HtmlParser(final String html, final String baseURI) {
        _document = getDocument(html, baseURI);
    }

    public HtmlParser(final InputStream is) {
        _document = getDocument(is, "");
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public Document getDocument() {
        return _document;
    }

    public Element getBody() {
        return null != _document ? _document.body() : null;
    }

    public Element getHead() {
        return null != _document ? _document.head() : null;
    }

    public Elements select(final String cssQuery) {
        return null != _document ? _document.select(cssQuery) : new Elements(0);
    }

    public String selectAsString(final String cssQuery) {
        final Elements result = this.select(cssQuery);
        return result.outerHtml();
    }

    public String remove(final String[] cssQueries) {
        for (final String cssQuery : cssQueries) {
            this.removeElements(cssQuery);
        }
        return null != _document ? _document.outerHtml() : "";
    }

    public String remove(final String cssQuery) {
        this.removeElements(cssQuery);
        return null != _document ? _document.outerHtml() : "";
    }

    public Document cleanBasic() {
        final Cleaner cleaner = new Cleaner(Whitelist.basic());
        return cleaner.clean(_document);
    }

    public String cleanBasicAsString() {
        final Cleaner cleaner = new Cleaner(Whitelist.basic());
        return cleaner.clean(_document).outerHtml();
    }

    public Document cleanRelaxed() {
        final Cleaner cleaner = new Cleaner(Whitelist.relaxed());
        return cleaner.clean(_document);
    }

    public String cleanRelaxedAsString() {
        final Cleaner cleaner = new Cleaner(Whitelist.relaxed());
        return cleaner.clean(_document).outerHtml();
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private Elements removeElements(final String cssQuery) {
        final Elements elements = _document.select(cssQuery);
        final Elements removed = elements.remove();
        return removed;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static Document getDocument(final URL uri, final int timeout) {
        try {
            return Jsoup.parse(uri, timeout);
        } catch (Throwable t) {
            LoggingUtils.getLogger(HtmlParser.class).log(Level.SEVERE, null, t);
        }
        return null;
    }

    private static Document getDocument(final String html, final String baseUri) {
        try {
            return Jsoup.parse(
                    new ByteArrayInputStream(html.getBytes()),
                    CHARSET,
                    baseUri
            );
        } catch (Throwable t) {
            LoggingUtils.getLogger(HtmlParser.class).log(Level.SEVERE, null, t);
        }
        return null;
    }

    private static Document getDocument(final InputStream is, final String baseUri) {
        try {
            return Jsoup.parse(
                    is,
                    CHARSET,
                    baseUri
            );
        } catch (Throwable t) {
            LoggingUtils.getLogger(HtmlParser.class).log(Level.SEVERE, null, t);
        }
        return null;
    }

    public static Elements select(final String html, final String cssQuery) {
        final HtmlParser parser = new HtmlParser(html);
        return parser.select(cssQuery);
    }

    public static String remove(final String html, final String cssQuery) {
        final HtmlParser parser = new HtmlParser(html);
        return parser.remove(cssQuery);
    }

    public static String remove(final String html, final String[] cssQuery) {
        final HtmlParser parser = new HtmlParser(html);
        return parser.remove(cssQuery);
    }
}
