package org.smartly.packages.sitebuilder.launcher;

import org.smartly.launcher.SmartlyLauncher;
import org.smartly.packages.SmartlyPackageLoader;
import org.smartly.packages.htmlparser.SmartlyHtmlParser;
import org.smartly.packages.sitebuilder.SmartlySiteBuilder;
import org.smartly.packages.velocity.SmartlyVelocity;


public class Main extends SmartlyLauncher {

    public Main(final String[] args) {
        super(args);
    }

    @Override
    protected void onLoadPackage(final SmartlyPackageLoader loader) {
        //-- main package --//
        loader.register(new SmartlySiteBuilder());

        //-- dependency packages --//
        loader.register(new SmartlyVelocity());
        loader.register(new SmartlyHtmlParser());
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
