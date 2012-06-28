package org.smartly.packages.sitebuilder;

import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.packages.AbstractPackage;
import org.smartly.packages.htmlparser.SmartlyHtmlParser;
import org.smartly.packages.sitebuilder.config.Deployer;
import org.smartly.packages.sitebuilder.impl.model.ModelDeployer;
import org.smartly.packages.velocity.SmartlyVelocity;

/**
 * SmartlySiteBuilder is a site generator based on velocity template engine.
 */
public class SmartlySiteBuilder extends AbstractPackage {

    public static final String NAME = "smartly_sitebuilder";

    public SmartlySiteBuilder() {
        super(NAME, 101);
        super.setDescription("Site Generator Module");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- module dependencies --//
        super.addDependency(SmartlyVelocity.NAME, ""); // all versions
        super.addDependency(SmartlyHtmlParser.NAME, ""); // all versions

        //-- lib dependencies --//

    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    @Override
    public void load() {
        Smartly.register(new Deployer(Smartly.getConfigurationPath()));
    }

    @Override
    public void ready() {
        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        final JSONObject configuration = Smartly.getConfiguration().getJSONObject("sitebuilder");
        _PATH_TEMPLATES = Smartly.getAbsolutePath(JsonWrapper.getString(configuration, "templates"));
        _PATH_OUTPUT = Smartly.getAbsolutePath(JsonWrapper.getString(configuration, "output"));
        _PATH_MODELS = Smartly.getAbsolutePath(JsonWrapper.getString(configuration, "models"));

        // ensure dir exists
        try {
            FileUtils.mkdirs(_PATH_TEMPLATES);
            FileUtils.mkdirs(_PATH_OUTPUT);
            FileUtils.mkdirs(_PATH_MODELS);
        } catch (Throwable ignored) {
        }

        // deploy models
        final ModelDeployer deployer = new ModelDeployer(_PATH_MODELS);
        deployer.deploy();
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String _PATH_MODELS;
    private static String _PATH_TEMPLATES;
    private static String _PATH_OUTPUT;

    public static String getPathModels() {
        return _PATH_MODELS;
    }

    public static String getPathTemplates() {
        return _PATH_TEMPLATES;
    }

    public static String getPathOutput() {
        return _PATH_OUTPUT;
    }

}
