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
        _DOMAIN = JsonWrapper.getString(configuration, "domain");
        _PATH_MODELS = JsonWrapper.getString(configuration, "models");
        _PATH_OUTPUT = JsonWrapper.getString(configuration, "output");
        _PATH_TEMPLATES = JsonWrapper.getString(configuration, "templates");
        _ABSOLUTE_PATH_TEMPLATES = Smartly.getAbsolutePath(_PATH_TEMPLATES);
        _ABSOLUTE_PATH_OUTPUT = Smartly.getAbsolutePath(_PATH_OUTPUT);
        _ABSOLUTE_PATH_MODELS = Smartly.getAbsolutePath(_PATH_MODELS);

        // ensure dir exists
        try {
            FileUtils.mkdirs(_ABSOLUTE_PATH_TEMPLATES);
            FileUtils.mkdirs(_ABSOLUTE_PATH_OUTPUT);
            FileUtils.mkdirs(_ABSOLUTE_PATH_MODELS);
        } catch (Throwable ignored) {
        }

        // deploy models
        final ModelDeployer deployer = new ModelDeployer(_ABSOLUTE_PATH_MODELS);
        deployer.setOverwrite(true);
        deployer.deploy();
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static String _DOMAIN;
    private static String _PATH_MODELS;
    private static String _PATH_TEMPLATES;
    private static String _PATH_OUTPUT;
    private static String _ABSOLUTE_PATH_MODELS;
    private static String _ABSOLUTE_PATH_TEMPLATES;
    private static String _ABSOLUTE_PATH_OUTPUT;

    public static String getDomain() {
        return _DOMAIN;
    }

    public static String getPathModels() {
        return _PATH_MODELS;
    }

    public static String getPathTemplates() {
        return _PATH_TEMPLATES;
    }

    public static String getPathOutput() {
        return _PATH_OUTPUT;
    }

    public static String getAbsolutePathModels() {
        return _ABSOLUTE_PATH_MODELS;
    }

    public static String getAbsolutePathTemplates() {
        return _ABSOLUTE_PATH_TEMPLATES;
    }

    public static String getAbsolutePathOutput() {
        return _ABSOLUTE_PATH_OUTPUT;
    }

}
