package org.smartly.packages.http.impl.util.vtool;


import org.smartly.commons.util.BeanUtils;
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

    private final String _resourcePath;
    private final HttpServletRequest _request;
    private final HttpServletResponse _response;

    private String _langCode;
    private String _userAgent;

    public Req(final String resourcePath, final HttpServletRequest httprequest, final HttpServletResponse httpresponse) {
        _resourcePath = resourcePath;
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
        return _resourcePath;
    }

    /**
     * Return request parameter or empty String
     *
     * @param name Name of parameter into POST or GET request
     * @return Object or empty String. Never null.
     */
    public String getParam(final String name) {
        return this.getParam(name, "");
    }

    public String getParam(final String name, final String def) {
        final Object param = this.getRawParam(name);
        if (null != param) {
            if (param.getClass().isArray()) {
                final Object[] array = (Object[]) param;
                return array[0].toString();
            }
            return param.toString();
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
            _langCode = getLang(_request);
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

    private Object getRawParam(final String name) {
        if (null != _request) {
            return _request.getParameterMap().get(name);
        }
        return null;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static String getLang(final HttpServletRequest request) {
        final String value = request.getHeader(HEADER_ACCEPT_LANGUAGE);
        final String[] tokens = StringUtils.split(value, ",");
        if (tokens.length > 0) {
            final Locale locale = LocaleUtils.getLocaleFromString(tokens[0]);
            return locale.getLanguage();
        }
        return LocaleUtils.getCurrent().getLanguage();
    }
}
