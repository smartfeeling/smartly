/*
 * 
 */
package org.smartly.packages.remoting.impl;

import org.smartly.Smartly;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Abstract proxy REST service.
 *
 * @author angelo.geminiani
 */
public abstract class RemoteService {

    private final String _name;
    private String _token; // security Token

    // ------------------------------------------------------------------------
    //                      Constructor
    // ------------------------------------------------------------------------
    public RemoteService(final String name) {
        _name = name;
    }

    // ------------------------------------------------------------------------
    //                      Public
    // ------------------------------------------------------------------------

    public String getName() {
        return _name;
    }

    public String getToken() {
        return _token;
    }

    public void setToken(String token) {
        this._token = token;
    }

    public boolean isValidToken(final String authToken) {
        if (StringUtils.hasText(authToken)) {
            return this.validateToken(authToken);
        } else if (StringUtils.hasText(_token)) {
            return this.validateToken(_token);
        }
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------
    protected Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    //
    private boolean validateToken(final String token) {
        if (StringUtils.hasText(token)) {
            final String smartlyToken = getAppToken();
            if (token.equalsIgnoreCase(smartlyToken)) {
                return true;
            } else {
                final String enc1 = decodeWhile(token);
                final String enc2 = decodeWhile(smartlyToken);
                return enc1.equalsIgnoreCase(enc2);
            }
        }
        return false;
    }

    public String decode(final String s) {
        try {
            return URLDecoder.decode(s, Smartly.getCharset());
        } catch (UnsupportedEncodingException ignored) {
        }
        return s;
    }

    private String decodeWhile(final String s) {
        String result = s;
        try {
            result = URLDecoder.decode(result, Smartly.getCharset());
            while (result.contains("%")) {
                try {
                    result = URLDecoder.decode(result, Smartly.getCharset());
                } catch (Throwable ignored) {
                    break;
                }
            }
        } catch (UnsupportedEncodingException ignored) {
        }
        return result;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String __APP_TOKEN = null;

    private static String getAppToken() {
        if (null == __APP_TOKEN) {
            __APP_TOKEN = Smartly.getConfiguration().getString("remoting.app_securetoken");
        }
        return null != __APP_TOKEN ? __APP_TOKEN : "";
    }

}
