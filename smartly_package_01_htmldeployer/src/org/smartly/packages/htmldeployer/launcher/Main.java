package org.smartly.packages.htmldeployer.launcher;

import org.smartly.launcher.SmartlyLauncher;
import org.smartly.packages.SmartlyPackageLoader;
import org.smartly.packages.htmldeployer.SmartlyHtmlDeployer;


public class Main extends SmartlyLauncher {

    public Main(final String[] args) {
        super(args);
    }

    @Override
    protected void onLoadPackage(final SmartlyPackageLoader loader) {
        loader.register(new SmartlyHtmlDeployer());
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    /**
     * Launcher main method.
     * Use this only for debug or testing purpose
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final Main main = new Main(args);
        main.run();
    }


}
