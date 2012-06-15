package org.smartly.launcher;

import org.smartly.packages.SmartlyPackageLoader;

/**
 * User: angelo.geminiani
 */
public class SmartlyLauncher
        extends AbstractLauncher {


    public SmartlyLauncher(final String[] args) {
        super(args);
    }

    @Override
    protected void onLoadPackage(final SmartlyPackageLoader loader) {
        // nothing to do here. only for packages
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    /**
     * Smartly main method. This retrieves the Smartly home directory, creates the
     * classpath and invokes run().
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final SmartlyLauncher main = new SmartlyLauncher(args);
        main.run();
    }
}
