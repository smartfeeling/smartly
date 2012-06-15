package org.smartly.packages.remoting;


import org.smartly.Smartly;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.remoting.config.Deployer;

public class SmartlyRemoting extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_remoting";

    public SmartlyRemoting() {
        super(NAME, 2);
        super.setVersion("0.0.1");
        super.setDescription("Remoting Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- module dependencies --//

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

    }
}
