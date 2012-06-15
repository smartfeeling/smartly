package org.smartly.packages.velocity;


import org.smartly.Smartly;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.velocity.config.Deployer;
import org.smartly.packages.velocity.impl.VLCManager;

public class SmartlyVelocity extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_velocity";

    public SmartlyVelocity() {
        super(NAME, 1);
        super.setDescription("Velocity Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- lib dependencies --//


    }

    @Override
    public void load() {
        Smartly.register(new Deployer(Smartly.getConfigurationPath()));
    }

    @Override
    public void ready() {
        final String docRoot = (String) Smartly.getConfiguration().get("velocity.doc_root");
        final String absolute = Smartly.getAbsolutePath(docRoot);
        // init velocity engine
        VLCManager.getInstance().getEngine().setFileResourceLoaderPath(absolute);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
