package org.smartly.packages.http.launcher;

import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.launcher.SmartlyLauncher;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.SmartlyPackageLoader;
import org.smartly.packages.http.SmartlyHttp;
import org.smartly.packages.velocity.SmartlyVelocity;


public class Main extends SmartlyLauncher {

    public Main(final String[] args) {
        super(args);
    }

    @Override
    protected void onLoadPackage(final SmartlyPackageLoader loader) {
        //-- main package --//
        loader.register(new SmartlyHttp());

        //-- system packages --//
        loader.register(new SmartlyVelocity());
        final AbstractPackage remoting = ClassLoaderUtils.optInstance("org.smartly.packages.remoting.SmartlyRemoting") ;
        loader.register(remoting);
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
