package org.smartly.packages.mail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.smartly.packages.mail.impl.MailUtils;
import org.smartly.packages.mail.launcher.Main;


public class SmartlyMailTest {

    @BeforeClass
    public static void init(){
        Main.main(new String[]{"-w", "z:/_smartly_mail"});
    }

    @Test
    public void testReady() throws Exception {
        MailUtils.sendMailTo("I'll be back [smartly@gmail.com]",
                "angelo.geminiani@gmail.com",
                "TEST",
                "This is a test").join();
    }

}
