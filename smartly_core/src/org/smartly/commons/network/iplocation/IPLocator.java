/*
 * 
 */
package org.smartly.commons.network.iplocation;

import org.smartly.commons.network.URLUtils;
import org.smartly.commons.util.FormatUtils;

/**
 * @author angelo.geminiani
 */
public final class IPLocator {

    private static final String URL_PATTERN = "http://api.hostip.info/get_html.php?ip={0}&position=true";

    private IPLocator() {
    }

    public IPLocation locate(final String ip) throws Exception {
        final String url = FormatUtils.format(URL_PATTERN, ip);
        final String response = URLUtils.getUrlContent(url);
        return new IPLocation(response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------
    private static IPLocator __instance;

    public static IPLocator getInstance() {
        if (null == __instance) {
            __instance = new IPLocator();
        }
        return __instance;
    }
}
