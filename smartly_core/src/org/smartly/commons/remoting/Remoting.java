package org.smartly.commons.remoting;

import org.smartly.Smartly;

/**
 *
 */
public class Remoting {


    public static String getAppToken() {
        return Smartly.getConfiguration().getString("remoting.app_securetoken");
    }

}
