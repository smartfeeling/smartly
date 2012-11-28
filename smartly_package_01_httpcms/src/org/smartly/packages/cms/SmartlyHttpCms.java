package org.smartly.packages.cms;


import org.smartly.Smartly;
import org.smartly.commons.io.jsonrepository.JsonRepository;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.cms.config.Deployer;
import org.smartly.packages.cms.impl.cms.endpoint.CMSRouter;
import org.smartly.packages.http.SmartlyHttp;
import org.smartly.packages.mongo.SmartlyMongo;
import org.smartly.packages.velocity.SmartlyVelocity;

/**
 *
 */
public class SmartlyHttpCms extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_cms";

    public SmartlyHttpCms() {
        super(NAME, 1);
        super.setVersion("0.0.1");
        super.setDescription("Http CMS Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- module dependencies --//
        super.addDependency(SmartlyVelocity.NAME, ""); // all versions
        super.addDependency(SmartlyMongo.NAME, ""); // all versions
        super.addDependency(SmartlyHttp.NAME, ""); // all versions

        //-- lib dependencies --//

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

    private static CMSRouter __cms;

    static {
        LoggingRepository.getInstance().setLogFileName(SmartlyHttpCms.class, "./smartly_cms.log");
    }

    public static Logger getCMSLogger() {
        return LoggingUtils.getLogger(CMSRouter.class);
    }

    public static void registerCMSEndPoint(final CMSRouter cms) {
        __cms = cms;
    }

    public static CMSRouter getCMS() {
        if (null == __cms) {
            __cms = new CMSRouter();
        }
        return __cms;
    }

    private static JsonRepository getConfiguration() throws Exception {
        if (null == __config) {
            __config = Smartly.getConfiguration(true);
        }
        return __config;
    }

}
