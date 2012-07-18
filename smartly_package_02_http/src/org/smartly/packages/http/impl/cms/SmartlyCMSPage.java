package org.smartly.packages.http.impl.cms;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONObject;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.velocity.impl.VLCManager;

/**
 *
 */
public class SmartlyCMSPage {


    private static final String VHTML_DIRECTIVE = "<!-- [vhtml] -->";

    private final String _url;
    private final VelocityEngine _vengine;
    private final VelocityContext _vcontext;
    private JSONObject _localizations;
    private String _head;
    private String _header;
    private String _content;
    private String _footer;

    public SmartlyCMSPage(final SmartlyCMSPage page,
                          final VelocityEngine engine,
                          final VelocityContext context) {
        _vcontext = context;
        _vengine = engine;
        _url = page._url;
        _localizations = page._localizations;
        _head = page._head;
        _header = page._header;
        _content = page._content;
        _footer = page._footer;
    }

    public SmartlyCMSPage(final String url) {
        _vcontext = null;
        _vengine = null;
        _url = url;
    }

    public String getUrl() {
        return _url;
    }

    public String getContent() {
        if (null!=_vcontext && null!=_vengine && isVhtml(_content)) {
            return merge(_url, _content, _vengine, _vcontext);
        }
        return _content;
    }

    public void setContent(String _content) {
        this._content = _content;
    }

    public String getFooter() {
        if (null!=_vcontext && null!=_vengine && isVhtml(_footer)) {
            return merge(_url, _footer, _vengine, _vcontext);
        }
        return _footer;
    }

    public void setFooter(String _footer) {
        this._footer = _footer;
    }

    public String getHead() {
        if (null!=_vcontext && null!=_vengine && isVhtml(_head)) {
            return merge(_url, _head, _vengine, _vcontext);
        }
        return _head;
    }

    public void setHead(String _head) {
        this._head = _head;
    }

    public String getHeader() {
        if (null!=_vcontext && null!=_vengine && isVhtml(_header)) {
            return merge(_url, _header, _vengine, _vcontext);
        }
        return _header;
    }

    public void setHeader(String _header) {
        this._header = _header;
    }

    public JSONObject getLocalizations() {
        return _localizations;
    }

    public void setLocalizations(JSONObject _localizations) {
        this._localizations = _localizations;
    }

    public String label(final String lang, final String key) {
        return JsonWrapper.getString(_localizations, lang.concat(".").concat(key), "");
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return SmartlyCMS.getLogger();
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static boolean isVhtml(final String text) {
        return StringUtils.hasText(text) && text.length() > VHTML_DIRECTIVE.length() && text.substring(0, VHTML_DIRECTIVE.length()).equalsIgnoreCase(VHTML_DIRECTIVE);
    }

    private static String merge(final String url,
                                final String text,
                                final VelocityEngine engine,
                                final VelocityContext context) {
        try {
            return VLCManager.getInstance().evaluateText(engine, url, text, context);
        } catch (Throwable ignored) {
        }
        return text;
    }
}
