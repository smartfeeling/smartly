package org.smartly.packages.htmlparser;


import org.smartly.Smartly;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;

public class SmartlyHtmlParser extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_htmlparser";

    public SmartlyHtmlParser() {
        super(NAME, 1);
        super.setVersion("0.0.1");
        super.setDescription("HTML Parser  Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- lib dependencies --//
        super.addDependency("org.jsoup:jsoup:1.6.3", "");
    }

    @Override
    public void load() throws Exception {
        this.init();
    }

    @Override
    public void ready() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // Smartly.register(new Deployer(Smartly.getConfigurationPath()));
    }
}
