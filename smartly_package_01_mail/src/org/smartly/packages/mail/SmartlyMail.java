package org.smartly.packages.mail;


import org.smartly.Smartly;
import org.smartly.commons.jsonrepository.JsonRepository;
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

    public static String getFrom() throws Exception {
        return (String) getConfiguration().get("mail.smtp.reply_to");
    }

    public static String getHost() throws Exception {
        return (String) getConfiguration().get("mail.smtp.host");
    }

    public static int getPort() throws Exception {
        return (Integer) getConfiguration().get("mail.smtp.port");
    }

    public static String getUsername() throws Exception {
        return (String) getConfiguration().get("mail.smtp.username");
    }

    public static String getPassword() throws Exception {
        return (String) getConfiguration().get("mail.smtp.password");
    }

    public static boolean getTLS() throws Exception {
        return (Boolean) getConfiguration().get("mail.smtp.TLS");
    }

    public static boolean isDebug() throws Exception {
        return (Boolean) getConfiguration().get("mail.smtp.debug");
    }


}
