package org.smartly.packages.mongo;


import org.smartly.Smartly;
import org.smartly.SmartlyPathManager;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.ISmartlySystemPackage;
import org.smartly.packages.mongo.config.Deployer;

public class SmartlyMongo extends AbstractPackage
        implements ISmartlySystemPackage {

    public static final String NAME = "smartly_mongo";

    public SmartlyMongo() {
        super(NAME, 1);
        super.setDescription("MongoDB Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- lib dependencies --//
        super.addDependency("org.mongodb:mongo-java-driver:2.7.3", "");
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
        final String configurationPath = SmartlyPathManager.getConfigurationPath(SmartlyMongo.class);
        Smartly.register(new Deployer(configurationPath));
    }

}
