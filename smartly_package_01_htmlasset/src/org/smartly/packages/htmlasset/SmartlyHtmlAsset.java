package org.smartly.packages.htmlasset;


import org.smartly.Smartly;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.htmlasset.config.Deployer;
import org.smartly.packages.htmlasset.impl.htdoc.HtmlDeployer;
import org.smartly.packages.htmldeployer.SmartlyHtmlDeployer;

/**
 *
 */
public class SmartlyHtmlAsset extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_httpasset";

    public SmartlyHtmlAsset() {
        super(NAME, 1);
        super.setVersion("0.0.1");
        super.setDescription("Http Assets Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- module dependencies --//
        super.addDependency(SmartlyHtmlDeployer.NAME, ""); // all versions

        //-- lib dependencies --//

    }

    @Override
    public void load() throws Exception {
        this.init();
    }

    @Override
    public void ready() {
        // deploy html files
        this.deployHtml();
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        Smartly.register(new Deployer(Smartly.getConfigurationPath()));


    }

    private void deployHtml() {
        final HtmlDeployer deployer = new HtmlDeployer();
        deployer.deploy();
    }
}
