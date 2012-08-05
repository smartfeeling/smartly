package org.smartly.packages.htmldeployer;

import org.junit.Test;
import org.smartly.packages.htmldeployer.impl.HtmlDeployer;


public class SmartlyHtmlDeployerTest {

    @Test
    public void testReady() throws Exception {

        HtmlDeployer deployer = new TestDeployer();
        deployer.setOverwrite(true);
        deployer.getPreProcessorFiles().add(".html");
        deployer.deploy();
    }

}
