package org.smartly.packages.htmlparser.impl.vtools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.smartly.Smartly;
import org.smartly.commons.lang.CharEncoding;
import org.smartly.commons.network.URLUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * DOM parser.
 */
public class DocumentTool {

    public static final String NAME = "document";

    private static final String PARAM_URL = "url";
    private static final String PARAM_CHARSET = "charset";

    private final Map<String, String> _requestParams;
    private final String _servletPath;
    private final String _charset;
    private final URL _url;
    private final String _protocol;
    private final String _host; // http://www.google.it
    private final int _port;
    private final String _path;
    private final org.jsoup.nodes.Document _document;
    private String _title;
    private String __domain;

    public DocumentTool(final String servletPath, final Map<String, String> params) throws Exception {
        _requestParams = params;
        _servletPath = servletPath;
        _charset = CharEncoding.isSupported(params.get(PARAM_CHARSET)) ? params.get(PARAM_CHARSET) : getDefaultCharset();
        _url = getUrl(params.get(PARAM_URL));
        _protocol = _url.getProtocol();
        _host = _url.getHost();
        _port = _url.getPort();
        _path = _url.getPath();

        // creates document
        final InputStream is = URLUtils.getInputStream(_url, 5000, URLUtils.TYPE_HTML);
        _document = Jsoup.parse(is, _charset, _url.toString());
        _title = _document.title();

    }

    public String getName() {
        return NAME;
    }

    @Override
    public String toString() {
        return null != _document ? _document.outerHtml() : super.toString();
    }

    //-- DOM --//

    public String getCharset() {
        return _charset;
    }

    public String getTitle() {
        return null != _title ? _title : "";
    }

    public org.jsoup.nodes.Document getDocument() {
        return _document;
    }

    public Element getBody() {
        return null != _document ? _document.body() : null;
    }

    public Element getHead() {
        return null != _document ? _document.head() : null;
    }

    public Elements select(final String selector) {
        return null != _document ? _document.select(selector) : new Elements(0);
    }

    public Elements select(final Element element, final String selector) {
        return null != element ? element.select(selector) : select(selector);
    }

    //-- Path --//

    public String getUrl() {
        return _url.toString();
    }

    public String getDomain() {
        if (null == __domain) {
            final StringBuilder result = new StringBuilder();
            if (StringUtils.hasText(_protocol)) {
                result.append(_protocol);
            } else {
                result.append("http");
            }
            result.append("://");
            result.append(_host);
            if (_port > 0 && _port != 80) {
                result.append(":").append(_port);
            }
            __domain = result.toString();
        }

        return __domain;
    }

    public String getPath() {
        return _path;
    }

    public boolean isInternal(final String path) {
        final String domain = this.getDomain();
        return path.startsWith(domain);
    }

    //-- Transforms --//

    public void remove(final String selector) {
        this.removeElements(null, selector);
    }

    public void remove(final Element element, final String selector) {
        this.removeElements(element, selector);
    }

    public void removeStyles() {
        this.removeStyles(null);
    }

    public void removeStyles(final Element element) {
        this.removeElements(element, "style");
        this.removeElements(element, "link[rel=stylesheet]");
    }

    /**
     * Check all relative urls (i.e. "./images/image.png")
     * and change it in absolute url (i.e. "http://www.mysite.com/images/image.png")
     */
    public void absolutizeImagePaths() {
        final Elements images = this.select("img");
        if (null != images && images.size() > 0) {
            for (final Element image : images) {
                if (image.hasAttr("src")) {
                    image.attr("src", this.resolveUrl(image.attr("src")));
                }
            }
        }
    }

    public void mobilizeLinks() {
        this.mobilizeLinks(true);
    }

    public void mobilizeLinks(final boolean excludeExternalLinks) {
        final Elements links = this.select("a");
        if (null != links && links.size() > 0) {
            for (final Element link : links) {
                if (link.hasAttr("href")) {
                    link.attr("href", this.mobilizeUrl(link.attr("href"), excludeExternalLinks));
                }
            }
        }
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void removeElements(final Element element, final String selector) {
        final Elements elements = this.select(element, selector);
        if (null != elements && elements.size() > 0) {
            elements.remove();
        }
    }


    private String resolveUrl(final String path) {
        if (StringUtils.hasText(path) && path.startsWith(".") || path.startsWith("/")) {
            final String compound = PathUtils.concat(this.getDomain(), path);
            return PathUtils.resolve(compound);
        }
        return path;
    }

    private String mobilizeUrl(final String path, final boolean excludeExternal) {
        if (!StringUtils.hasText(path) || (excludeExternal && !this.isInternal(path))) {
            return path;
        }

        final String url = this.resolveUrl(path);
        _requestParams.put(PARAM_URL, url);
        _requestParams.put(PARAM_CHARSET, this.getCharset());
        return PathUtils.addURIParameters(_servletPath, _requestParams, true);
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

    private static URL getUrl(final String url) {
        try {
            return new URL(url);
        } catch (Throwable ignored) {
            if (StringUtils.hasText(url)) {
                if (url.startsWith("www")) {
                    return getUrl("http://".concat(url));
                }
            }
        }
        // return default
        return null;
    }
}
