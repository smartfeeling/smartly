package org.smartly.packages.zxing;


import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;

public class SmartlyZxing extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_zxing";

    public SmartlyZxing() {
        super(NAME, 1);
        super.setVersion("0.0.1");
        super.setDescription("ZXing Module for barcodes and qrcodes");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- lib dependencies --//
        super.addDependency("com.google.zxing:core:2.0", "");
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
