package org.smartly.commons.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: angelo.geminiani
 */
public class CompareUtilsTest {

    public CompareUtilsTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testCompare() throws Exception {
        int i = 10;
        double d = 10.0;
        long l = 10;
        String s="10";
        String[] arr1 = {"a", "b"};
        String[] arr2 = {"a", "b"};
        String[] arr3 = {"a", "c"};

        int result = CompareUtils.compare(i, d);
        assertEquals(result, 0);

        result = CompareUtils.compare(i, l);
        assertEquals(result, 0);

        result = CompareUtils.compare(i, s);
        assertEquals(result, 0);

        s="11";
        result = CompareUtils.compare(i, s);
        assertEquals(result, -1);

        l=999999999;
        d=999999999.000;
        result = CompareUtils.compare(d,l);
        assertEquals(result, 0);

        assertTrue(CompareUtils.equals(arr1, arr2));
        assertFalse(CompareUtils.equals(arr1, arr3));
    }
}
