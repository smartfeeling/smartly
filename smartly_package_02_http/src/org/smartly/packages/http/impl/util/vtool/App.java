package org.smartly.packages.http.impl.util.vtool;


import org.smartly.Smartly;
import org.smartly.commons.util.PathUtils;
import org.smartly.packages.http.SmartlyHttp;

/**
 * Module: SmartlyHttp
 *
 */
public class App extends org.smartly.packages.velocity.impl.vtools.App {

    public final String getAppToken() {
        try {
            return getSmartlyAppToken();
        } catch (Throwable ignored) {
        }
        return "";
    }

    public String getHttpRoot() {
        try {
            return getSmartlyAppUrl();
        } catch (Throwable ignored) {
        }
        return "";
    }

    public String getHttpPath(final String path) {
        try {
            final String root = getHttpRoot();
            return PathUtils.join(root, path);
        } catch (Throwable ignored) {
        }
        return "";
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String __APP_TOKEN = null;
    private static String __APP_URL = null;

    private static String getSmartlyAppToken() {
        if (null == __APP_TOKEN) {
            __APP_TOKEN = Smartly.getConfiguration().getString("remoting.app_securetoken");
        }
        return __APP_TOKEN;
    }

    private static String getSmartlyAppUrl() {
        if (null == __APP_URL) {
            __APP_URL = SmartlyHttp.getHTTPUrl("");
        }
        return __APP_URL;
    }

}
