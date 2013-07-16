package org.smartly.commons.util;

import org.junit.Test;

/**
 * User: angelo.geminiani
 */
public class MathUtilsTest {

    public MathUtilsTest() {

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    @Test
    public void testRound() throws Exception {
        System.out.println("Round");

        double val = 150.56719999999999;
        double rounded = MathUtils.round(val, 2);
        System.out.println(val + " rounded = " + rounded);
    }
}
