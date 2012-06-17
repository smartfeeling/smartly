package org.smartly.packages.http.impl.util.vtool;


import org.eclipse.jetty.util.resource.Resource;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.velocity.impl.vtools.IVLCTool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class Req implements IVLCTool {

    public static final String NAME = "req";

    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEADER_USER_AGENT = "User-Agent";

    private final Resource _resource;
    private final HttpServletRequest _request;
    private final HttpServletResponse _response;

    private String _langCode;
    private String _userAgent;

    public Req(final Resource resource, final HttpServletRequest httprequest, final HttpServletResponse httpresponse) {
        _resource = resource;
        _request = httprequest;
        _response = httpresponse;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Return requested file path. i.e. "/pages/index.html"
     *
     * @return i.e. "/pages/index.html"
     */
    public String getFilePath() {
        return null != _resource ? _resource.getName() : "";
    }

    /**
     * Return request parameter or empty String
     *
     * @param name Name of parameter into POST or GET request
     * @return Object or empty String. Never null.
     */
    public Object getParam(final String name) {
        return this.getParam(name, "");
    }

    public Object getParam(final String name, final Object def) {
        if (null != _request) {
            final Object result = _request.getParameterMap().get(name);
            return null != result ? result : def;
        }
        return def;
    }

    public int getInt(final String paramName) {
        return this.getInt(paramName, 0);
    }

    public int getInt(final String paramName, final int def) {
        try {
            return ConversionUtils.toInteger(this.getParam(paramName), def);
        } catch (Throwable t) {
        }
        return def;
    }

    public long getLong(final String paramName) {
        return this.getLong(paramName, 0L);
    }

    public long getLong(final String paramName, final long def) {
        try {
            return ConversionUtils.toLong(this.getParam(paramName), def);
        } catch (Throwable t) {
        }
        return def;
    }

    public double getDouble(final String paramName) {
        return this.getDouble(paramName, 0d);
    }

    public double getDouble(final String paramName, final double def) {
        try {
            return ConversionUtils.toDouble(this.getParam(paramName), -1, def);
        } catch (Throwable t) {
        }
        return def;
    }

    public String getString(final String paramName) {
        return this.getString(paramName, "");
    }

    public String getString(final String paramName, final String def) {
        try {
            return this.getParam(paramName, def).toString();
        } catch (Throwable t) {
        }
        return def;
    }

    public String getLangCode() {
        if (StringUtils.hasText(_langCode)) {
            return _langCode;
        } else {
            final String value = _request.getHeader(HEADER_ACCEPT_LANGUAGE);
            final String[] tokens = StringUtils.split(value, ",");
            if (tokens.length > 0) {
                final Locale locale = LocaleUtils.getLocaleFromString(tokens[0]);
                _langCode = locale.getLanguage();
            }
        }
        return StringUtils.hasText(_langCode)
                ? _langCode
                : LocaleUtils.getCurrent().getLanguage();
    }

    public String getUserAgent() {
        if (StringUtils.hasText(_userAgent)) {
            return _userAgent;
        } else {
            _userAgent = _request.getHeader(HEADER_USER_AGENT);
        }
        return StringUtils.hasText(_userAgent)
                ? _userAgent
                : "";
    }

    public boolean isMobile() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("mobile") > -1 || this.isMobileApple() || this.isAndroid();
        }
        return false;
    }

    public boolean isIPhone() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("iphone") > -1;
        }
        return false;
    }

    public boolean isIPad() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("ipad") > -1;
        }
        return false;
    }

    public boolean isIPod() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("ipod") > -1;
        }
        return false;
    }

    public boolean isMobileApple() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return this.isIPad() || this.isIPhone() || this.isIPod();
        }
        return false;
    }

    public boolean isAndroid() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return useragent.indexOf("android") > -1;
        }
        return false;
    }

    public boolean isMobileAndroid() {
        final String useragent = this.getUserAgent().toLowerCase();
        if (StringUtils.hasText(useragent)) {
            return this.isAndroid() && useragent.indexOf("mobile") > -1;
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}
