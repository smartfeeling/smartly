package org.smartly.packages.sitebuilder.impl;

import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.packages.sitebuilder.SmartlySiteBuilder;
import org.smartly.packages.sitebuilder.impl.model.ModelDeployer;

/**
 *
 */
public class SiteBuilder {


    private SiteBuilder() {

    }

    /**
     * Creates new site template from existing model.
     *
     * @param modelName
     * @param templateName
     * @throws Exception If template already exists.
     */
    public void newTemplate(final String modelName, final String templateName) throws Exception {
        final String target = PathUtils.concat(SmartlySiteBuilder.getPathTemplates(), templateName);
        if (FileUtils.exists(target)) {
            throw new Exception("Template already Exists: " + templateName);
        }
        final ModelDeployer deployer = new ModelDeployer(modelName, target);
        // add compilable directives
        deployer.getCompilableValues().put("[SITE_NAME]", templateName);

        deployer.deployChildren();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static SiteBuilder __instance;

    public static SiteBuilder getInstance() {
        if (null == __instance) {
            __instance = new SiteBuilder();
        }
        return __instance;
    }

}
