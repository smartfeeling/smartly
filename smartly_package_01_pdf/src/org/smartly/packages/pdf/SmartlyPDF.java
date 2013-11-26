package org.smartly.packages.pdf;

import org.smartly.Smartly;
import org.smartly.commons.io.jsonrepository.JsonRepository;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.pdf.config.Deployer;

/**
 * SmartlyPDF controller
 */
public class SmartlyPDF extends AbstractPackage
        implements ISmartlySystemPackage {


    public static final String NAME = "smartly_pdf";

    public SmartlyPDF() {
        super(NAME, 1);
        super.setVersion("0.0.1");
        super.setDescription("PDF Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- lib dependencies --//
        super.addDependency("pdfbox", "");
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
        Smartly.register(new Deployer(Smartly.getConfigurationPath()));
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static JsonRepository __config;

    private static JsonRepository getConfiguration() throws Exception {
        if (null == __config) {
            __config = Smartly.getConfiguration(true);
        }
        return __config;
    }

    public static String getVer() throws Exception {
        return (String) getConfiguration().get("pdf.pdf.version");
    }


}
