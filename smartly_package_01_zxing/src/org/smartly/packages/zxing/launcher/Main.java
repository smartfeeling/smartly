package org.smartly.packages.zxing.launcher;

import org.smartly.launcher.SmartlyLauncher;
import org.smartly.packages.SmartlyPackageLoader;
import org.smartly.packages.zxing.SmartlyZxing;


public class Main extends SmartlyLauncher {

    private final SmartlyZxing _package;

    public Main(final String[] args) {
        super(args);
        _package = new SmartlyZxing();
    }

    @Override
    protected void onLoadPackage(final SmartlyPackageLoader loader) {
        loader.register(_package);
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
