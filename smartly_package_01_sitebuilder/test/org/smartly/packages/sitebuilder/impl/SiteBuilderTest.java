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

        System.out.println("-----------------------------------------------------------");
        System.out.println("\t\t DEPLOY");
        System.out.println("-----------------------------------------------------------");

        try{
            SiteBuilder.getInstance().newTemplate("test", "A");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        SiteBuilder.getInstance().buildSite("A");

    }
}
