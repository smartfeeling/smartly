/*
 * 
 */
package org.smartly.packages.remoting.impl.util;

import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.network.shorturl.impl.TinyUrl;
import org.smartly.commons.util.ConversionUtils;
import org.smartly.commons.util.RandomUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.packages.remoting.impl.RemoteService;

/**
 * system REST service
 * <p/>
 * http://localhost/rest/system/random?param1=rO0ABXQAOTIvQkVFaW5nLzN8YWRtaW5pc3RyYXRvcnwyMDBDRUIyNjgwN0Q2QkY5OUZENkY0RjBE%0AMUNBNTRENA%3D%3D&param2=6
 *
 * @author angelo.geminiani
 */
public class RSSystem
        extends RemoteService {

    public static final String NAME = "system";

    public RSSystem() {
        super(NAME);
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public Object getSettings(final String authToken) throws Exception {
        if (super.isValidToken(authToken)) {
            return Smartly.getConfiguration();
        }
        return new JSONObject();
    }

    public boolean isConnected() {
        return true;
    }

    public String shortURL(final String authToken,
                           final String url) throws Exception {
        if (super.isValidToken(authToken)) {
            final TinyUrl tiny = new TinyUrl();
            return tiny.getShortUrl(url);
        }
        return "";
    }

    /**
     * Random service. enerates random alphanumeric value <br/>
     * USAGE: <br/>
     * http://localhost/rest/system/random?param1=rO0ABXQAOTIvQkVFaW5nLzN8YWRtaW5pc3RyYXRvcnwyMDBDRUIyNjgwN0Q2QkY5OUZENkY0RjBE%0AMUNBNTRENA%3D%3D&param2=6
     *
     * @param authToken
     * @param size      (Optional) size of returned code
     * @return
     */
    public String random(final String authToken,
                         final String size) {
        if (super.isValidToken(authToken)) {
            final char[] chars = "abcdefghilmnopqrstuvzxywjk0123456789".toCharArray();
            if (StringUtils.hasText(size)) {
                return RandomUtils.random(ConversionUtils.toInteger(size),
                        RandomUtils.CHARS_LOW_NUMBERS);
            } else {
                return RandomUtils.random(6,
                        RandomUtils.CHARS_LOW_NUMBERS);
            }
        }
        return "";
    }

    /**
     * Generates and returns a GUID
     *
     * @param authToken
     * @return
     */
    public String guid(final String authToken) {
        if (super.isValidToken(authToken)) {
            return GUID.create(true);
        }
        return "";
    }


}
