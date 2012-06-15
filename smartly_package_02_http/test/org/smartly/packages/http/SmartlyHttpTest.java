package org.smartly.packages.http;

import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.packages.http.launcher.Main;


public class SmartlyHttpTest {

    @BeforeClass
    public static void init() {
        Main.main(new String[]{"-w", "z:/_smartly_http"});
    }

    @Test
    public void testJetty() throws Exception {


    }


}
