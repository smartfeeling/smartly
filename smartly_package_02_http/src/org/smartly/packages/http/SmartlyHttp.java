package org.smartly.packages.http;


import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlyModalPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.http.config.Deployer;
import org.smartly.packages.http.impl.WebServer;
import org.smartly.packages.velocity.SmartlyVelocity;

/**
 * This package must be started ( load() method ) before application packages and must be ready ( method ready() )
 * after all other packages.
 */
public class SmartlyHttp
        extends AbstractPackage
        implements ISmartlySystemPackage, ISmartlyModalPackage {

    public static final String NAME = "smartly_http";

    public SmartlyHttp() {
        super(NAME, 2);
        super.setDescription("Http Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- module dependencies --//
        super.addDependency(SmartlyVelocity.NAME, ""); // all versions

        //-- lib dependencies --//
        super.addDependency("org.mongodb:mongo-java-driver:2.7.3", "");
    }

    @Override
    public void load() {
        Smartly.register(new Deployer(Smartly.getConfigurationPath()));
    }

    @Override
    public void ready() {
        this.init();
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void init() {
        //-- web server settings --//
        final boolean enabled = (Boolean) Smartly.getConfiguration().get("http.webserver.enabled");
        if (enabled) {
            final JSONObject configuration = (JSONObject) Smartly.getConfiguration().get("http.webserver");
            final String docRoot = JsonWrapper.getString(configuration, "root");
            final String absoluteDocRoot = Smartly.getAbsolutePath(docRoot);

            //-- the web server --//
            this.startWebserver(absoluteDocRoot, configuration);
        }
    }

    private void startWebserver(final String docRoot, final JSONObject configuration) {
        try {
            // ensure resource base exists
            FileUtils.mkdirs(docRoot);

            final WebServer server = new WebServer(docRoot, configuration);
            server.start();

        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE, null, t);
        }
    }

}
