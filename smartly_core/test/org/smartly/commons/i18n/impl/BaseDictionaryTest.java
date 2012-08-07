package org.smartly.commons.i18n.impl;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * User: angelo.geminiani
 */
public class BaseDictionaryTest {

    public BaseDictionaryTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testGetMessage() throws Exception {
          TestDic dic = new TestDic();

        String label_de = dic.getMessage("label", "de");
        String file_de = dic.getMessage("file", "de");
        assertEquals(label_de, "(de)");
        assertEquals(file_de, "<h1>de</h1>");

        String label_jp = dic.getMessage("label", "");
        String file_jp = dic.getMessage("file", "");
        assertEquals(label_jp, "(DEFAULT) THIS IS A LABEL");
        assertEquals(file_jp, "<h1>default</h1>");

        String file_err = dic.getMessage("error", "");
        assertEquals(file_err, "nofile.html");
    }
}
