package org.smartly.packages.mail;


import org.smartly.Smartly;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.mail.config.Deployer;

/**
 * Simple Mail util module.
 * Use MailUtils class to send email messages using smtp.
 */
public class SmartlyMail extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_mail";

    public SmartlyMail() {
        super(NAME, 1);
        super.setVersion("0.0.1");
        super.setDescription("Java Mail Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- lib dependencies --//
        super.addDependency("com.sun.mail:javax.mail:1.4.5", "");
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

}
