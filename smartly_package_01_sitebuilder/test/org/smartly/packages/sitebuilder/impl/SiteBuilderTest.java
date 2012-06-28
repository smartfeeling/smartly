package org.smartly.packages.sitebuilder.impl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.packages.sitebuilder.impl.model.ModelDeployer;
import org.smartly.packages.sitebuilder.launcher.Main;

/**
 * User: angelo.geminiani
 */
public class SiteBuilderTest {

    public SiteBuilderTest() {

    }

    @BeforeClass
    public static void open(){
        Main.main(new String[]{"-w", "z:/_smartly_sitebuilder/"});
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testNewTemplate() throws Exception {

        SiteBuilder.getInstance().newTemplate("test", "A");

    }
}
