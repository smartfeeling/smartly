package org.smartly;

import org.smartly.packages.SmartlyPackageLoader;

/**
 * JVM Shutdown Hook
 */
public class SmartlyShutdownHook extends Thread {

    private final SmartlyPackageLoader _packageLoader;

    public SmartlyShutdownHook(final SmartlyPackageLoader packageLoader) {
        _packageLoader = packageLoader;
    }

    // call unload() when the JVM is closing
    @Override
    public void run() {
        if (null != _packageLoader) {
            _packageLoader.unload();
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
