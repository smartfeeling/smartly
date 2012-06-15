package org.smartly.packages.htmldeployer;


import org.smartly.Smartly;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.htmldeployer.config.Deployer;

public class SmartlyHtmlDeployer extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_htmldeployer";

    public SmartlyHtmlDeployer() {
        super(NAME, 1);
        super.setVersion("0.0.1");
        super.setDescription("Html, js, css deployer and compressor.");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- lib dependencies --//

    }

    @Override
    public void load() throws Exception {
        // register configuration deployer
        Smartly.register(new Deployer(Smartly.getConfigurationPath()));
    }

    @Override
    public void ready() {

    }

}
