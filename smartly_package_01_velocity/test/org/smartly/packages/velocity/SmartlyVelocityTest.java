package org.smartly.packages.velocity;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.Smartly;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.packages.velocity.launcher.Main;
import org.smartly.packages.velocity.impl.VLCManager;

import java.io.File;
import java.util.HashMap;


public class SmartlyVelocityTest {

    @BeforeClass
    public static void init() {
        Main.main(new String[]{"-w", "z:/_smartly_velocity"});
    }

    @Test
    public void testMain() throws Exception {

        final String docRoot = Smartly.getAbsolutePath((String) Smartly.getConfiguration().get("velocity.doc_root"));
        final String templatePath = PathUtils.join(docRoot, "test.vt");
        final String template = new String(FileUtils.copyToByteArray(new File(templatePath)));
        Assert.assertNotNull(template);

        System.out.println("TEMPLATE:");
        System.out.println(template);

        final String resolved = VLCManager.getInstance().evaluateText("test", template, new HashMap<String, Object>());
        System.out.println("RESOLVED:");
        System.out.println(resolved);
        Assert.assertNotNull(resolved);
        Assert.assertFalse(resolved.equalsIgnoreCase(template));
    }

}
