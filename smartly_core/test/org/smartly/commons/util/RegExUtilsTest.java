package org.smartly.commons.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class RegExUtilsTest {

    @Test
    public void testReplaceNoAlphanumericChar() throws Exception {
        String result = RegExUtils.replaceNoAlphanumericChar("test file");
        System.out.println(result);
        assertEquals(result, "testfile");
    }

}
